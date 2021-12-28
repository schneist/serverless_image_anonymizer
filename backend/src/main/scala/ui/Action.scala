package ui

import cats.data.EitherT
import cats.effect.IO
import cats.implicits.*
import cats.Functor
import typings.node.bufferMod.global.Buffer
import typings.sharp.mod.{OverlayOptions, Region, Sharp}
import typings.tensorflowModelsBlazeface.faceMod.*
import typings.tensorflowModelsBlazeface.mod.BlazeFaceConfig
import typings.tensorflowTfjsCore.distTensorMod.{Tensor3D, Tensor4D}

import scala.concurrent.{ExecutionContext, Future, blocking}
import scala.scalajs.js
import scala.scalajs.js.JSConverters.*
import scala.scalajs.js.typedarray.Uint8Array
import scala.util.{Failure, Success, Try}

case class BoundingBox(Height: Int, Width: Int, Left: Int, Top: Int)

object BoundingBox{
  def from : NormalizedFace => Either[Throwable,BoundingBox] = (nf :NormalizedFace) => Try{
    val topLeft : scala.scalajs.js.Tuple2[Double, Double] = nf.topLeft.asInstanceOf[scala.scalajs.js.Tuple2[Double, Double]]
    val botR : scala.scalajs.js.Tuple2[Double, Double] =  nf.bottomRight.asInstanceOf[scala.scalajs.js.Tuple2[Double, Double]]
    BoundingBox((botR._1-topLeft._1).toInt,(botR._2-topLeft._2).toInt,(topLeft._1).toInt,(topLeft._2).toInt)
  }.toEither
}

trait Action[Environment, Input, Output, +ActionError] {

  def execute[Err>: ActionError](i: Input)(using environment: Environment): EitherT[IO, Err, Output]

}

object SharpToPNGBufferAction extends Action[EnvironmentWithExecutionContext,Sharp,Buffer,GenericErrors] {

  override def execute[Err >: GenericErrors](
                                              i: Sharp
                                            )(
                                              using environment: EnvironmentWithExecutionContext
                                            ): EitherT[IO, Err, Buffer] = {
    implicit val ec = environment.getExecutionContext()
    EitherT(
      IO.fromFuture(IO(
        i.png().toBuffer().toFuture
          .transform { _ match {
            case s: Success[Buffer] ⇒ Try(Right(s.get))
            case f: _ ⇒ Try(Left(UnknownError))
          }}
      ))
    )

  }
}


object FileWriteAction extends Action[EnvironmentWithExecutionContext,(Buffer,String),Unit,LoadImageErrors] {
  override def execute[Err >: LoadImageErrors](input: (Buffer,String))
                                              (using environment: EnvironmentWithExecutionContext,

                                              ): EitherT[IO, Err, Unit] = {
    implicit val ec = environment.getExecutionContext()
    EitherT(
      IO.fromFuture(IO(
        typings.node.fsPromisesMod.writeFile(input._2,input._1).toFuture.map(_.asInstanceOf[Unit])
          .transform { _ match {
            case s: Success[Unit] ⇒ Try(Right(()))
            case f: _ ⇒ Try(Left(FileNotFound()))
          }}
      ))
    )
  }
}

object BlurAction extends Action[EnvironmentWithExecutionContext,(Sharp,Seq[BoundingBox]),Sharp,GenericErrors] {


  override def execute[Err >: GenericErrors](i: (Sharp, Seq[BoundingBox]))(using environment: EnvironmentWithExecutionContext): EitherT[IO, Err, Sharp] = {
    implicit val ec = environment.getExecutionContext()
    EitherT(
      IO.fromFuture(IO(removeSubImages(i._1,i._2).transform { _ match {
        case s: Success[Sharp] ⇒ Try(Right(s.get))
        case f: _ ⇒ Try(Left(UnknownError))
      }}
      ))
    )
  }

  def removeSubImages(sharp: Sharp, bounds: Seq[BoundingBox])(implicit ec:ExecutionContext): Future[Sharp] = {
    val blur = true || js.Dynamic.global.process.env.BLUR.asInstanceOf[js.UndefOr[String]].toOption.exists(_.equalsIgnoreCase("true"))
    val radius = js.Dynamic.global.process.env.BLUR_RADIUS.asInstanceOf[js.UndefOr[Double]].toOption.getOrElse(25.0)
    for {
      m <- sharp.metadata().toFuture
      extracted <- bounds.map(b => {
        val boxWidth = b.Width match {
          case belowZero: Int if belowZero <= 0 => 0
          case good: Int => good
        }
        val boxHeight = b.Height match {
          case belowZero: Int if belowZero <= 0 => 0
          case good: Int => good
        }
        val boxLeft = b.Left match {
          case belowZero: Int if belowZero <= 0 => 0
          case good: Int => good
        }
        val boxTop = b.Top match {
          case belowZero: Int if belowZero <= 0 => 0
          case good: Int => good
        }
        val filler: Future[typings.node.bufferMod.global.Buffer] = if (blur) {
          sharp.toBuffer().toFuture.flatMap(b => typings.sharp.mod.apply(b)
            .extract(Region.apply(boxHeight, boxLeft, boxTop, boxWidth))
            .blur(radius).toBuffer().toFuture)
        } else {
          sharp.toBuffer().toFuture.flatMap(b => typings.sharp.mod.apply(b)
            .extract(Region.apply(boxHeight, boxLeft, boxTop, boxWidth))
            .threshold(255).toBuffer().toFuture)
        }
        filler.map(x => OverlayOptions.apply().setInput(x).setLeft(boxLeft).setTop(boxTop))
      }).toList.traverse(identity)
      out <- Future.successful(sharp.withMetadata().composite(extracted.toJSArray))
    } yield out
  }
}

object BoundingBoxAction extends Action[EnvironmentWithExecutionContext,js.Array[NormalizedFace],Seq[BoundingBox] ,GenericErrors]{
  override def execute[Err >: GenericErrors](i:  js.Array[NormalizedFace])
                                           (using environment: EnvironmentWithExecutionContext
                                           ): EitherT[IO, Err,Seq[BoundingBox]] = {
    EitherT.apply(IO(i.map(BoundingBox.from).toList.traverse(identity).left.map(_ ⇒ UnknownError)))

  }
}

object CreateSharpAction extends Action[EnvironmentWithExecutionContext,Uint8Array,Sharp ,SharpErrors]{

  override def execute[Err >: SharpErrors](i: Uint8Array)
                                          (using environment: EnvironmentWithExecutionContext
                                          ): EitherT[IO, Err,Sharp] = {

    EitherT.pure(typings.sharp.mod.apply(i,typings.sharp.mod.SharpOptions.apply().setFailOnError(true)))
  }
}

object EstimateFacesAction extends Action[EnvironmentWithExecutionContext,(Tensor3D | Tensor4D,BlazeFaceModel) ,js.Array[NormalizedFace],EstimationErrors]{

  override def execute[Err >: EstimationErrors](i:(Tensor3D | Tensor4D,BlazeFaceModel))
                                               (using environment: EnvironmentWithExecutionContext
                                               ): EitherT[IO, Err,js.Array[NormalizedFace]] = {
    typings.tensorflowTfjsNode.mod.backend()
    implicit val ec = environment.getExecutionContext()
    EitherT(
      IO.fromFuture(IO(i._2.estimateFaces(i._1.asInstanceOf[Tensor3D]).toFuture.transform { _ match {
        case s: Success[js.Array[NormalizedFace]] ⇒ Try(Right(s.get))
        case f: _ ⇒ Try(Left(FaceNotFound))
      }}))
    )
  }
}

object ModelLoadAction extends Action[EnvironmentWithExecutionContext,Unit,BlazeFaceModel,TFErrors]{

  override def execute[Err >: TFErrors](i: Unit)
                                       (using environment: EnvironmentWithExecutionContext
                                       ): EitherT[IO, Err,BlazeFaceModel] = {
    typings.tensorflowTfjsNode.mod.backend()
    implicit val ec = environment.getExecutionContext()
    EitherT(
      IO.fromFuture(IO(typings.tensorflowModelsBlazeface.mod.load(BlazeFaceConfig().setScoreThreshold(0.01).setMaxFaces(10)).toFuture.transform { _ match {
        case s: Success[BlazeFaceModel] ⇒ Try(Right(s.get))
        case f: _ ⇒ Try(Left(TFModelNotFound))
      }}))
    )
  }
}

object ExtractImageDataAction extends Action[EnvironmentWithExecutionContext,Uint8Array,Tensor3D | Tensor4D ,ConversionErrors]{

  override def execute[Err >: ConversionErrors](i: Uint8Array)
                                               (using environment: EnvironmentWithExecutionContext
                                               ): EitherT[IO, Err,Tensor3D | Tensor4D] = {
    typings.tensorflowTfjsNode.mod.backend()
    EitherT.pure(typings.tensorflowTfjsNode.nodeMod.node.decodeImage(i))
  }
}

object LoadImageAction extends Action[EnvironmentWithExecutionContext,String,Uint8Array,LoadImageErrors]{

  override def execute[Err >: LoadImageErrors](i: String)
                                              (using environment: EnvironmentWithExecutionContext,
                                              ): EitherT[IO, Err, Uint8Array] = {
    implicit val ec = environment.getExecutionContext()
    EitherT(
      IO.fromFuture(IO(
        typings.node.fsPromisesMod.readFile(i).toFuture.map(_.asInstanceOf[Uint8Array])
          .transform { _ match {
            case s: Success[Uint8Array] ⇒ Try(Right(s.get))
            case f: _ ⇒ Try(Left(FileNotFound()))
          }}
      ))
    )
  }

  def checkFileAccess(filepath: String)
                     (using environment: EnvironmentWithExecutionContext,
                     ):cats.data.EitherT[cats.effect.IO, AnonymizationErrors, Unit] = {
    implicit val ec = environment.getExecutionContext()
    EitherT {
      IO.fromFuture(IO(
        typings.node.fsPromisesMod.access(filepath).toFuture.transform {
          _ match {
            case f: Failure[Unit] ⇒ Try(Left(FileNotFound()))
            case s: Success[Unit] ⇒ Try(Right(()))
          }
        }
      ))
    }
  }
}

trait Environment

trait EnvironmentWithExecutionContext extends Environment{
  def getExecutionContext():ExecutionContext
}

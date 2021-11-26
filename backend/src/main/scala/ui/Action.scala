package ui

import cats.{Functor, MonadError}
import cats.data.EitherT
import cats.effect.IO
import typings.node.bufferMod.global.Buffer
import typings.tensorflowTfjsCore.distTensorMod.{Tensor3D, Tensor4D}

import java.io.FileNotFoundException
import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js.typedarray.Uint8Array
import scala.util.{Failure, Success, Try}

trait Action[Environment, Input, Output, +ActionError] {

  def execute[Err>: ActionError](i: Input)(using environment: Environment): EitherT[IO, Err, Output]

}
sealed trait AnonymizationErrors
sealed trait LoadImageErrors extends AnonymizationErrors
class FileNotFound extends LoadImageErrors



object ExtractImageDataAction extends Action[EnvironmentWithExecutionContext,Uint8Array,Tensor3D | Tensor4D ,ConversionErrors]{

  override def execute[Err >: ConversionErrors](i: Uint8Array)
                                               (using environment: EnvironmentWithExecutionContext
                                               ): EitherT[IO, Err,Tensor3D | Tensor4D] = {
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
          .transform { _
          match {
            case s: Success[Uint8Array] ⇒ Try(Right(s.get))
            case f: _ ⇒ Try(Left(FileNotFound()))
          }
          }
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

sealed trait ConversionErrors extends AnonymizationErrors
case object GenericConversionError extends ConversionErrors


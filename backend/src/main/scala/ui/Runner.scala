package ui
import org.scalablytyped.runtime.StObject
import org.scalajs.dom.raw
import org.scalajs.dom.raw.{FileReader, HTMLCanvasElement}
import org.w3c.dom.html.HTMLImageElement
import org.w3c.dom.*
import typings.canvas.mod
import typings.canvas.mod.*
import typings.cloudevents.cloudeventMod.CloudEvent
import typings.node.bufferMod.global
import typings.node.bufferMod.global.BufferEncoding.utf8
import typings.node.nodeStrings
import typings.node.utilMod.TextEncoder
import typings.sharp.mod.{OverlayOptions, Region, Sharp}
import typings.tensorflowTfjsCore.distTensorMod.*
import typings.tensorflowTfjsNode.mod.*
import scalajs.js.JSConverters.JSRichFutureNonThenable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.language.postfixOps
import scala.math.Integral.Implicits.infixIntegralOps
import scala.math.Numeric.Implicits.infixNumericOps
import scala.scalajs.js
import scala.scalajs.js.JSConverters.{JSRichFutureThenable, iterableOnceConvertible2JSRichIterableOnce}
import scala.scalajs.js.Promise.reject
import scala.scalajs.js.typedarray.{Uint16Array, Uint8Array}
import scala.scalajs.js.{JSON, Promise, typedarray}
import scala.util.Try
import cats.implicits.*
import typings.tensorflowModelsBlazeface.faceMod.NormalizedFace
import ui.Anonymizer.*
object Runner  {

  def main(args: Array[String]): Unit = {
    //ToDo:: remove workaround to var $i_$0040tensorflow$002ftfjs$002dnode = require("@tensorflow/tfjs-node");
    typings.tensorflowTfjsNode.mod.backend()
    val iurl= """/images/stefan.jpg"""
    val aa = for {
      model ← typings.tensorflowModelsBlazeface.mod.load().toFuture
      image ← typings.node.fsPromisesMod.readFile(iurl).toFuture
      imageData ← Future.apply(typings.tensorflowTfjsNode.nodeMod.node.decodeImage(image.asInstanceOf[Uint8Array]))
      forecast ← model.estimateFaces(imageData.asInstanceOf[Tensor3D]).toFuture //ToDo:: match instead of cast
      sharp ← Future.successful(typings.sharp.mod.apply(image,typings.sharp.mod.SharpOptions.apply().setFailOnError(true)))
      meta ← sharp.metadata().toFuture.map(a ⇒ JSON.stringify(a)).map(println)
      boxes ← forecast.map(BoundingBox.from).toList.traverse(identity).fold(Future.failed,Future.apply) // move to EitherT
      removed ← removeSubImages(sharp,boxes)
      buffer ← removed.png().toBuffer().toFuture
      write ← typings.node.fsPromisesMod.writeFile( """/images/stefan_a.jpg""",buffer).toFuture
    } yield write
    aa.onComplete(_ ⇒ println("finished"))
  }
}

object Anonymizer {

  case class BoundingBox(Height: Int, Width: Int, Left: Int, Top: Int)

  object BoundingBox{
    def from : NormalizedFace => Either[Throwable,BoundingBox] = (nf :NormalizedFace) => Try{
      val topLeft : scala.scalajs.js.Tuple2[Double, Double] = nf.topLeft.asInstanceOf[scala.scalajs.js.Tuple2[Double, Double]]
      val botR : scala.scalajs.js.Tuple2[Double, Double] =  nf.asInstanceOf[scala.scalajs.js.Tuple2[Double, Double]]
      BoundingBox((botR._1-topLeft._1).toInt,(botR._2-topLeft._2).toInt,(topLeft._1).toInt,(topLeft._2).toInt)
    }.toEither
  }

  def removeSubImages(sharp: Sharp, bounds: Seq[BoundingBox]): Future[Sharp] = {
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
        val filler: Future[ typings.node.bufferMod.global.Buffer] = if (blur) {
          sharp.toBuffer().toFuture.flatMap(b => typings.sharp.mod.apply(b)
            .extract(Region.apply(boxHeight,boxLeft, boxTop, boxWidth))
            .blur(radius).toBuffer().toFuture)
        } else {
          sharp.toBuffer().toFuture.flatMap(b => typings.sharp.mod.apply(b)
            .extract(Region.apply(boxHeight,boxLeft, boxTop, boxWidth))
            .threshold(255).toBuffer().toFuture)
        }
        filler.map(x => OverlayOptions.apply().setInput(x).setLeft(boxLeft).setTop(boxTop))
      }).toList.traverse(identity)
      out <- Future.successful(sharp.withMetadata().composite(extracted.toJSArray))
    } yield out
  }

}
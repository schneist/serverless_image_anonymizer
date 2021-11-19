package ui

import typings.awsLambda.apiGatewayProxyMod.*
import typings.awsLambda.s3Mod.*
import typings.cloudevents.cloudeventMod.CloudEvent
import typings.koushFaceApiJs.fetchImageMod._
import typings.koushFaceApiJs.mod._
import typings.canvas.mod._
import org.scalablytyped.runtime.*
import typings.sharp.mod._

import javax.management.monitor.MonitorNotification
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js.JSConverters.*
import scala.scalajs.js.annotation.*
import scala.scalajs.js.*
import scala.util.Try
import cats.implicits._

import scala.scalajs._
/**
 object AWSAPIGatewayEventFunctionsHandler {

  @JSExportTopLevel(name = "LambdaHandler")
  def handle(APIGatewayEvent: APIGatewayEvent) :Promise[APIGatewayProxyResult] = {

    val body = ""
    val headers : StringDictionary[Boolean | Double | String] = StringDictionary.apply(( "Content-Type", "text/html"))
    val response = APIGatewayProxyResult(body = body,statusCode = 200)
    response.headers = headers
    Future.apply(response).toJSPromise
  }


  object Anonymizer {

    case class BoundingBox(Height: Option[Int], Width: Option[Int], Left: Option[Int], Top: Option[Int])

    def removeSubImages(sharp: Sharp, bounds: Seq[BoundingBox]): Future[Sharp] = {
      val blur = js.Dynamic.global.process.env.BLUR.asInstanceOf[js.UndefOr[String]].toOption.exists(_.equalsIgnoreCase("true"))
      val radius = js.Dynamic.global.process.env.BLUR_RADIUS.asInstanceOf[js.UndefOr[Double]].toOption.getOrElse(25.0)
      for {
        m <- sharp.metadata().toFuture
        h <- Future.fromTry(Try {
          m.height.get match {
            case belowZero: Double if belowZero <= 0 => 0
            case good: Double => good
          }
        })
        w <- Future.fromTry(Try {
          m.width.get match {
            case belowZero: Double if belowZero <= 0 => 0
            case good: Double => good
          }
        })
        extracted <- bounds.map(b => {
          val boxWidth = (b.Width.get * w) match {
            case belowZero: Double if belowZero <= 0 => 0
            case good: Double => good
          }
          val boxHeight = (b.Height.get * h) match {
            case belowZero: Double if belowZero <= 0 => 0
            case good: Double => good
          }
          val boxLeft = (b.Left.get * w) match {
            case belowZero: Double if belowZero <= 0 => 0
            case good: Double => good
          }
          val boxTop = (b.Top.get * h) match {
            case belowZero: Double if belowZero <= 0 => 0
            case good: Double => good
          }
          val filler: Future[typings.node.Buffer] = if (blur) {
            sharp.toBuffer().toFuture.flatMap(b => typings.sharp.mod.apply(b)
              .extract(Region.apply(boxLeft, boxTop, boxWidth, boxHeight))
              .blur(radius).toBuffer().toFuture)
          } else {
            sharp.toBuffer().toFuture.flatMap(b => typings.sharp.mod.apply(b)
              .extract(Region(boxLeft, boxTop, boxWidth, boxHeight))
              .threshold(255).toBuffer().toFuture)
          }
          filler.map(x => OverlayOptions.apply().setInput(x).setLeft(boxLeft).setTop(boxTop))
        }).toList.traverse(identity)
        out <- Future.successful {
          sharp.withMetadata().composite(extracted.toJSArray)
        }
      } yield out
    }

  }
/***
    val o = for{
      r_M <- rekognition_SDK.detectLabels(DetectLabelsRequest(Image(js.undefined,S3Object( e.bucket.name,filename)))).promise().toFuture
      labels <- Future.successful(traverse(ujson.read(js.JSON.stringify(r_M.Labels)),"Name"))
      confidences <- Future.successful(traverse(ujson.read(js.JSON.stringify(r_M.Labels)),"Confidence"))
      _ <- dynamo_SDK.putItem(PutItemInput(Dictionary("Filename" -> AttributeValue(S=filename),"RekognitionLabels"-> AttributeValue.S(labels.mkString),"Confidences" -> AttributeValue.S(confidences.mkString),"raw" -> AttributeValue.S(js.JSON.stringify(r_M.Labels))),e.bucket.name)).promise().toFuture
      s3 <- s3_SDK.getObject(GetObjectRequest( e.bucket.name,filename)).promise().toFuture
      faceList <- Future.successful(r_M.Labels.get.filter(_.Name.equals("Person")).flatMap(_.Instances.getOrElse(Array.empty[Instance].toJSArray)))
      sharp <-(s3.Body.get:Any) match {
        case s:String =>  Future.successful(typings.sharp.mod.apply(s))
        case a:scala.scalajs.js.Array[scala.Byte] =>  Future.successful(typings.sharp.mod.apply(a.map(_.toInt).join("")))
        case _:Any => {
          Future.successful(typings.sharp.mod.apply(""))
        }
      }
      removed <- removeSubImages(sharp,faceList.map(_.BoundingBox.getOrElse(BoundingBox(0.0F,0.0F,0.0F,0.0F))).toSeq) //.filter(_.Height.getOrElse(0.0F)>=0))
      buffer <- removed.toBuffer().toFuture
      out <-s3_SDK.putObjectFuture(PutObjectRequest(e.bucket.name,filename.replace("input","output"),js.undefined,buffer.toString()))
    } yield out
    o.map[Unit](_ => ()).toJSPromise
  }
**/
}
**/


package ui
import typings.canvas.mod.*
import typings.cloudevents.cloudeventMod.CloudEvent
import typings.koushFaceApiJs.anon.PartialEnvironment
import typings.koushFaceApiJs.mod.*

import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.scalajs.js
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.math.Integral.Implicits.infixIntegralOps
import scala.scalajs.js.Promise
import scala.scalajs.js.Promise.reject

object Runner  {

  val MODEL_URL = """/models"""



  def main(args: Array[String]): Unit = {
    val fetch : (String, scala.scalajs.js.UndefOr[typings.std.RequestInit]) => scala.scalajs.js.Promise[typings.std.Response] = (s:String,r:scala.scalajs.js.UndefOr[typings.std.RequestInit] ) ⇒ {
      r.map(rr ⇒ typings.crossFetch.mod.fetch(s,rr)).getOrElse({println("Blubb");reject(Promise)})
    }
    val pe = PartialEnvironment()
    pe.setFetch(fetch)
    env.monkeyPatch(env = pe )
    val aa = for {
      a <- loadSsdMobilenetv1Model(MODEL_URL).toFuture
      b <- loadFaceLandmarkModel(MODEL_URL).toFuture
      c <- loadFaceRecognitionModel(MODEL_URL).toFuture
    } yield a

    js.timers.setTimeout(500) {
      println(aa.value)
    }
  }
}

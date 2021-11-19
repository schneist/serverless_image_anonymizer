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
      val ff  = r.map(rr ⇒ typings.crossFetch.mod.fetch(s,rr))
      ff.getOrElse({
        println("Fetch returned undef")
        reject(Promise)
      })
    }
    val createcanvas :() => typings.std.HTMLCanvasElement = ??? //typings.canvas.mod.createCanvas()
    val pe = PartialEnvironment()
    pe.setFetch(fetch)
    pe.setCreateCanvasElement(createcanvas)
    env.monkeyPatch(env = pe )
    val aa = for {
      a <- nets.ssdMobilenetv1.loadFromDisk(MODEL_URL).toFuture
      c <- nets.faceRecognitionNet.loadFromDisk(MODEL_URL).toFuture
    } yield (a,c)

    js.timers.setTimeout(500) {
      println(aa.value)
    }
  }
}

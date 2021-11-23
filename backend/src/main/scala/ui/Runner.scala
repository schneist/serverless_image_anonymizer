package ui
import org.scalablytyped.runtime.StObject
import org.scalajs.dom.raw
import org.scalajs.dom.raw.{FileReader, HTMLCanvasElement}
import org.w3c.dom.{Attr, Document, NamedNodeMap, Node, NodeList, TypeInfo, UserDataHandler}
import org.w3c.dom.html.HTMLImageElement
import typings.canvas.mod
import typings.canvas.mod.*
import typings.cloudevents.cloudeventMod.CloudEvent
import typings.node.nodeStrings
import typings.node.bufferMod.global.BufferEncoding.utf8
import typings.node.utilMod.TextEncoder
import typings.tensorflowTfjsCore.distTensorMod.Tensor3D

import scala.language.postfixOps
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.scalajs.js
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.math.Integral.Implicits.infixIntegralOps
import scala.math.Numeric.Implicits.infixNumericOps
import scala.scalajs.js.{JSON, Promise, typedarray}
import scala.scalajs.js.Promise.reject
import scala.scalajs.js.typedarray.{Uint16Array, Uint8Array}

object Runner  {
  val MODEL_URL = """/models"""

  def main(args: Array[String]): Unit = {
    val fetch : (String, scala.scalajs.js.UndefOr[org.scalajs.dom.experimental.RequestInit]) => scala.scalajs.js.Promise[org.scalajs.dom.experimental.Response] = (s:String,r:scala.scalajs.js.UndefOr[org.scalajs.dom.experimental.RequestInit] ) ⇒ {
      val ff  = r.map(rr ⇒ typings.crossFetch.mod.fetch(s,rr))
      ff.getOrElse({
        println("Fetch returned undef")
        reject(Promise)
      })
    }
    println(typings.tensorflowTfjsBackendCpu.mod.versionCpu)
    val iurl= """/images/stefan.jpg"""
    val aa = for {
      input ← typings.canvas.mod.loadImage(iurl).toFuture
      canvas ← Future.apply(typings.canvas.mod.createCanvas(width = input.width,height =input.height))
      ctx ← Future.apply(canvas.getContext_2d(typings.canvas.canvasStrings.`2d`))
      cc ← Future.apply(ctx.drawImage(input,0,0,input.width,input.height))
      model ← typings.tensorflowModelsBlazeface.mod.load().toFuture
      imageData ← Future.apply(typings.tensorflowTfjsNode.imageMod.decodeImage(canvas.toBuffer().asInstanceOf[Uint8Array]))
      forecast ← Future.apply(model.estimateFaces(imageData.asInstanceOf[Tensor3D]))
    } yield forecast
    js.timers.setTimeout(5000) {
      println("dfasd")

      println("asd"+aa.value)
    }
  }
}


/***

import * as faceapi from 'face-api.js';

import { canvas, faceDetectionNet, faceDetectionOptions, saveFile } from './commons';

async function run() {

  await faceDetectionNet.loadFromDisk('../../weights')

  const img = await canvas.loadImage('../images/bbt1.jpg')
  const detections = await faceapi.detectAllFaces(img, faceDetectionOptions)

  const out = faceapi.createCanvasFromMedia(img) as any
  faceapi.draw.drawDetections(out, detections)

  saveFile('faceDetection.jpg', out.toBuffer('image/jpeg'))
  console.log('done, saved results to out/faceDetection.jpg')
}

run()

 */

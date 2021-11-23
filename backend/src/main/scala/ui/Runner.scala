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
import typings.tensorflowTfjsCore.distTensorMod.{Tensor3D, Tensor4D}
import typings.tensorflowTfjsNode.mod.*

import scala.language.postfixOps
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.scalajs.js
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.math.Integral.Implicits.infixIntegralOps
import scala.math.Numeric.Implicits.infixNumericOps
import scala.scalajs.js.JSConverters.iterableOnceConvertible2JSRichIterableOnce
import scala.scalajs.js.{JSON, Promise, typedarray}
import scala.scalajs.js.Promise.reject
import scala.scalajs.js.typedarray.{Uint16Array, Uint8Array}

object Runner  {
  val MODEL_URL = """/models"""

  def main(args: Array[String]): Unit = {
    //ToDo:: remove workaround to var $i_$0040tensorflow$002ftfjs$002dnode = require("@tensorflow/tfjs-node");
    typings.tensorflowTfjsNode.mod.backend()
    val iurl= """/images/stefan.jpg"""
    val aa = for {
      model ← typings.tensorflowModelsBlazeface.mod.load().toFuture
      image ← typings.node.fsPromisesMod.readFile(iurl).toFuture
      imageData ← Future.apply(typings.tensorflowTfjsNode.nodeMod.node.decodeImage(image.asInstanceOf[Uint8Array]))
      forecast ←  model.estimateFaces(imageData.asInstanceOf[Tensor3D]).toFuture
    } yield forecast
    js.timers.setTimeout(5000) {
      aa.map(a ⇒ println(JSON.stringify(a)))
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

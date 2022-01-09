package action
import cats.MonadError
import cats.data.{EitherT, EitherTMonad}
import cats.effect.*
import cats.effect.unsafe.IORuntime.global
import org.scalatest.funspec.*
import ui.{LoadImageAction, *}

import scala.concurrent.{Await, ExecutionContext, ExecutionContextExecutor, Future}
import cats.implicits.*
import cats.data.EitherT.*
import cats.data.*
import cats.*

import javax.swing.text.ElementIterator
import scala.util.{Failure, Success, Try}
import org.scalatest.matchers.should.*
import org.scalatest.freespec.AsyncFreeSpec
import cats.effect.testing.scalatest.AsyncIOSpec
import typings.node.bufferMod.global.Buffer

import scala.language.postfixOps
import scala.Unit
import scala.scalajs.js.JSON
import scala.scalajs.js.typedarray.Uint8Array


class ActionTest extends AsyncFunSpec with AsyncIOSpec with Matchers{



  given  EnvironmentWithExecutionContext  = new EnvironmentWithExecutionContext {
    override def getExecutionContext(): ExecutionContext = scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
  }

  implicit override def executionContext = scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

  val existingPath = "images/stefan.png"
  val nonExistingPath = "images/stefans.png"

  describe("An ImageAction") {
    describe("when checking") {
      describe("an non existing File") {
        it("should return a domain excption") {
          LoadImageAction.checkFileAccess(existingPath).value.asserting( _.isRight shouldBe true)
        }
      }
      describe("an existing File") {
        it("should return unit") {
          LoadImageAction.checkFileAccess(nonExistingPath).value.asserting( _.isLeft shouldBe true)
        }
      }
    }
    describe("when opening") {
      describe("an  existing File") {
        it("should return the File Content as UInt8Array") {
          LoadImageAction.execute(existingPath).value.asserting( _.isRight shouldBe true)
        }
      }
      describe("an non existing File") {
        it("should return a domain excption") {
          LoadImageAction.execute(nonExistingPath).value.asserting( _.isLeft shouldBe true)
        }
      }
    }
  }
  describe("An ExtractImageDataAction") {
    describe("when transforming") {
      it("should return a Tensor") {
        val aa = for {
          array <- LoadImageAction.execute(existingPath)
          b <- ExtractImageDataAction.execute(array)
        } yield array
        aa.value.asserting(_.isRight shouldBe true)
      }
    }
  }
  describe(" A ModelLoadAction") {
    describe("when loading") {
      it("should return a Model") {
        ModelLoadAction.execute(()).value.asserting( _.isRight shouldBe true)
      }
    }
  }
  describe("An EstimateFaceAction") {
    describe("when estimating") {
      it("should find a face") {
        val aa = for {
          array <- LoadImageAction.execute(existingPath)
          imageData <- ExtractImageDataAction.execute(array)
          model ← ModelLoadAction.execute(())
          estimate ← EstimateFacesAction.execute((imageData,model))
        } yield estimate
        aa.value.asserting(_.fold(_ ⇒ false,estimation ⇒ {
          estimation.length == 1 &&
            estimation.head.probability.get.toString.toDouble >= 0.1
        }) shouldBe true)
      }
    }
  }

  describe("An AnonymizationFlow ") {

    it("should anonymize a face") {
      val outp = """images/stefan_anon.png"""
      val aa = for {
        array <- LoadImageAction.execute(existingPath)
        imageData <- ExtractImageDataAction.execute(array)
        model ← ModelLoadAction.execute(())
        estimate ← EstimateFacesAction.execute((imageData,model))
        sharp ← CreateSharpAction.execute(array)
        boxes ← BoundingBoxAction.execute(estimate)
        blurred ← BlurAction.execute((sharp,boxes))
        png ← SharpToPNGBufferAction.execute(blurred)
        written ← FileWriteAction.execute((png,outp))
        loaded <- LoadImageAction.checkFileAccess(outp)
      } yield loaded
      aa.value.asserting(_.isRight shouldBe true)
    }
  }
}





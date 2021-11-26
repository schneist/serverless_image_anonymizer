package action
import cats.MonadError
import cats.data.{EitherT, EitherTMonad}
import cats.effect.*
import org.scalatest.funspec.*
import ui.{AnonymizationErrors, ConversionErrors, EnvironmentWithExecutionContext, ExtractImageDataAction, FileNotFound, LoadImageAction, LoadImageErrors}

import concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, ExecutionContext, ExecutionContextExecutor, Future}
import cats.implicits.*
import cats.data.EitherT._
import cats.data.*
import cats._
import javax.swing.text.ElementIterator
import scala.util.{Failure, Success, Try}
import org.scalatest.matchers.should.*
import org.scalatest.freespec.AsyncFreeSpec
import cats.effect.testing.scalatest.AsyncIOSpec
import typings.node.bufferMod.global.Buffer

import scala.language.postfixOps
import scala.Unit
import scala.scalajs.js.typedarray.Uint8Array


class ActionTest extends AsyncFunSpec with AsyncIOSpec with Matchers{



  given  EnvironmentWithExecutionContext  = new EnvironmentWithExecutionContext {
    override def getExecutionContext(): ExecutionContext = scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
  }




  implicit override def executionContext = scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

  val existingPath = "/images/stefan.png"
  val nonExistingPath = "/images/stefans.png"

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
      describe("an non existing File") {
        it("should return a domain excption") {
         LoadImageAction.execute(existingPath).value.asserting( _.isRight shouldBe true)
        }
      }
      describe("an existing File") {
        it("should return the File Content as UInt8Array") {
          LoadImageAction.execute(nonExistingPath).value.asserting( _.isLeft shouldBe true)
        }
      }
    }
  }
  describe("An ExtractImageDataAction") {
    describe("when transforming") {
      it("should retunr a Tensor") {
        val aa = for {
          array <- LoadImageAction.execute(existingPath)
          b <- ExtractImageDataAction.execute(array)
        } yield array
        aa.value.asserting(_.isRight shouldBe true)
      }
    }
  }
}


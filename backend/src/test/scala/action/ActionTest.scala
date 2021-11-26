package action
import cats.MonadError
import cats.data.EitherT
import cats.effect.IO
import org.scalatest.funspec.*
import ui.{EnvironmentWithExecutionContext, FileNotFound, LoadImageAction, LoadImageErrors}

import concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, ExecutionContext, ExecutionContextExecutor, Future}
import cats.implicits.*

import javax.swing.text.ElementIterator
import scala.util.{Failure, Success, Try}
import org.scalatest.matchers.should.*
import org.scalatest.freespec.AsyncFreeSpec
import cats.effect.testing.scalatest.AsyncIOSpec
import typings.node.bufferMod.global.Buffer

import scala.language.postfixOps
import scala.Unit
import ui.FileNotFound

import scala.scalajs.js.typedarray.Uint8Array


class ActionTest extends AsyncFunSpec with AsyncIOSpec with Matchers{

  given  EnvironmentWithExecutionContext  = new EnvironmentWithExecutionContext {
    override def getExecutionContext(): ExecutionContext = scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
  }

  val me = summon[cats.MonadError[cats.effect.IO, LoadImageErrors]]

  given cats.MonadError[cats.effect.IO, LoadImageErrors] = me

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
}


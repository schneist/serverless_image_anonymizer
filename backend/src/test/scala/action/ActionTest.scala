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

import scala.language.postfixOps
import scala.Unit
import ui.FileNotFound

class ActionTest extends AsyncFunSpec with AsyncIOSpec with Matchers{

  given  EnvironmentWithExecutionContext  = new EnvironmentWithExecutionContext {
    override def getExecutionContext(): ExecutionContext = scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
  }

  val me = summon[cats.MonadError[cats.effect.IO, LoadImageErrors]]

  given cats.MonadError[cats.effect.IO, LoadImageErrors] = me

  implicit override def executionContext = scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

  describe("An ImageAction") {
    describe("when accessing") {
      describe("an non existing File") {
        it("should return a daomain excption") {
          LoadImageAction.checkFileAccess("/images/stefans.png").value.asserting( _.isLeft shouldBe true)
        }
      }
      describe("an existing File") {
        it("should return unit") {
          LoadImageAction.checkFileAccess("/images/stefan.png").value.asserting( _.isRight shouldBe true)
        }
      }
    }
  }
}

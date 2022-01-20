import cats.*
import cats.data.EitherT.*
import cats.data.*
import cats.effect.*
import cats.effect.testing.scalatest.AsyncIOSpec
import cats.effect.unsafe.IORuntime.global
import cats.implicits.*
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.funspec.*
import org.scalatest.matchers.should.*
import ui.*

import javax.swing.text.ElementIterator
import scala.Unit
import scala.concurrent.{Await, ExecutionContext, ExecutionContextExecutor, Future}
import scala.language.postfixOps
import scala.scalajs.js.JSON
import scala.scalajs.js.typedarray.Uint8Array
import scala.util.{Failure, Success, Try}


class ActionTest extends AsyncFunSpec with AsyncIOSpec with Matchers {


  describe("React") {

    describe("render a component") {
      it("should return a string") {
        println(Main.handle())
        Main.handle().length should be > 0
      }
    }
  }


}

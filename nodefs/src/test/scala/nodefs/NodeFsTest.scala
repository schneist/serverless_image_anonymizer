package nodefs

import org.scalatest.funspec.{AnyFunSpec, AsyncFunSpec}
import org.scalatest.matchers.should.Matchers
import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js.Promise
class NodeFsTest extends AsyncFunSpec  with Matchers{

  implicit override def executionContext = scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

  describe("basic file ops in tmp") {

    it("should write and read the same in a text file file") {
      val WR :Future[typings.node.bufferMod.global.Buffer] = for {
        f <- NodeFSStorage.openRW("/tmp/test.txt").toFuture
        g <- NodeFSStorage.write(f,typings.node.bufferMod.global.Buffer.from("Test")).toFuture
        _ <- f.close().toFuture
        ff <- NodeFSStorage.openR("/tmp/test.txt").toFuture
        h <- NodeFSStorage.read(ff).toFuture
      } yield h
      WR.map( _.toString shouldBe "Test")
    }
  }
}
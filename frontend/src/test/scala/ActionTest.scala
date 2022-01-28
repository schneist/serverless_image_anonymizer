
import japgolly.scalajs.react
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import org.scalatest.funspec.*
import org.scalatest.matchers.should.*
import ui.*
import ui.FileObjectComponent.Props

import javax.swing.text.ElementIterator
import scala.Unit
import scala.concurrent.{Await, ExecutionContext, ExecutionContextExecutor, Future}
import scala.language.postfixOps
import scala.scalajs.js.JSON
import scala.scalajs.js.typedarray.Uint8Array
import scala.util.{Failure, Success, Try}
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import japgolly.scalajs.react.ReactMonocle.*
import japgolly.scalajs.react.StateAccessor.{ReadImpureWritePure, ReadWrite, ReadWritePure}
import japgolly.scalajs.react.component.builder.Lifecycle
import japgolly.scalajs.react.extra.*
import japgolly.scalajs.react.util.Effect.Id
import japgolly.scalajs.react.util.Util.identityFn
import nodefs.NodeFSStorage

class ActionTest extends AnyFunSpec with Matchers {






  describe("React") {

    describe("render a component") {
      it("should return a string") {
        val ret = ReactDOMServer.renderToString(japgolly.scalajs.react.vdom.html_<^.<.div(Top.Component(NodeFSStorage)))//Seq(File("."),File(".."),Folder("Home",Seq.empty[FileObject])))))
        println(ret)
       // "1".length should be 1
      }
    }
  }


}

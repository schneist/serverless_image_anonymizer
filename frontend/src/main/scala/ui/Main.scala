package ui
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import scala.scalajs.js.Promise
import scala.scalajs.js.annotation.JSExportTopLevel

object Main {

  @JSExportTopLevel(name = "Main")
  def handle(): String = {

    val ret = ReactDOMServer.renderToString(<.div(Ticker.Timer()))
    println(ret)
    ret

  }
}

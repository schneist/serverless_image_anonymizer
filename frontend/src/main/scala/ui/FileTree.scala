package ui

import scalajs.js
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import japgolly.scalajs.react.ReactCats.*
import japgolly.scalajs.react.extra._
import monocle._
import japgolly.scalajs.react.ReactMonocle._

sealed trait FileThingy{
  def displayName() :String
  def displaySize(): Option[Int]
}

final case class File(name:String,size: Int) extends FileThingy {

  override def displayName() = name

  override def displaySize(): Option[Int] = Some(size)

}

final case class Directory(name:String) extends FileThingy {

  override def displayName() = name

  override def displaySize() = None
}

object FileThingy {
  val size = Lens[FileThingy,Option[Int]   ](_.displaySize())(x => {
    case f: File => f.copy(size = x.getOrElse(0))
    case d: Directory => d
  })
  val name = Lens[FileThingy, String](_.displayName())(x => {
    case f: File => f.copy(name = x)
    case d: Directory => d.copy(name = x)
  })
}






object FileObjectComponent {

  type State = FileThingy

  final case class Props(ss: StateSnapshot[State]) {
    @inline def render = Comp(this)
  }

  given reusabilityState: Reusability[State] = Reusability.derive

  given reusabilityProps: Reusability[Props] = Reusability.derive


   val Comp = ScalaComponent.builder[Props]
    .renderBackend[Backend]
     .configure(Reusability.shouldComponentUpdate)
    .build


  final class Backend(bs: BackendScope[Props, Unit]) {

    private val ssNameFn = StateSnapshot.withReuse.zoomL(FileThingy.name).prepareViaProps(bs)(_.ss)

    def render(p: Props): VdomElement = <.div(p.ss.value.displayName())
    /*
    <.ul(
      s.toTagMod {
        case f: File => <.li(f.displayName)
        case d: Folder => <.li(d.displayName, TreeComponent(d.files))
      }
    )
     */


  }

}









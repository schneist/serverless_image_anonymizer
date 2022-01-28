package ui

import scalajs.js
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import japgolly.scalajs.react.ReactCats.*
import japgolly.scalajs.react.extra.*
import monocle.*
import japgolly.scalajs.react.ReactMonocle.*
import shared.FileDescriptorStorage
import ui.FileObjectComponent.State

import scala.scalajs.js.Promise

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

  final case class Props(ss: StateSnapshot[State]){
    @inline def render = Comp(ss.value)(this)
  }

  given reusabilityState: Reusability[State] = Reusability.derive

  given reusabilityProps: Reusability[Props] = Reusability.derive


  val Comp = (f:FileThingy) => ScalaComponent.builder[Props]
    .initialState(f)
    .renderBackend[Backend]
    .configure(Reusability.shouldComponentUpdate)
    .build


  final class Backend(bs: BackendScope[Props, State]) {

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

object Top {

  type State = FileThingy
  type Props = FileDescriptorStorage[Promise]

  final class Backend($: BackendScope[Props, State]) {

    def render(state: State): VdomElement = {
      given reusabilityStateFT: Reusability[FileThingy] = Reusability.derive
      FileObjectComponent.Props(japgolly.scalajs.react.extra.internal.StateSnapshot.withReuse.apply(state).readOnly).render
    }
  }

  def Component  = ScalaComponent.builder[FileDescriptorStorage[Promise]]
    .initialState(Directory("/").asInstanceOf[FileThingy])
    .renderBackend[Backend]
    .build
}








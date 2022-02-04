package ui

import scalajs.js
import japgolly.scalajs.react.{ScalaComponent, *}
import japgolly.scalajs.react.vdom.html_<^.*
import japgolly.scalajs.react.ReactCats.*
import japgolly.scalajs.react.extra.*
import monocle.*
import japgolly.scalajs.react.ReactMonocle.*
import shared.FileDescriptorStorage
import ui.FileObjectComponent.{Props, State}

import scala.scalajs.js.Promise


/**
 Tree of Structure Thingy and  Content Thingy as seen from the UI
 */
sealed trait ContentOrganizationThingy  {

  /**
   *The following are considered first class properties (defining the nature of a file to be displayed)
   * and therefore implemented as inherited properties.
   * All  non defining properties shall be type classes
   */

  /**
   * The name of the FileThingy
   */
  def displayName() :String

  /**
   * Information IF it has a size as the number of Bytes
   */
  //ToDo:: size on Disk ....
  def displaySizeNumBytes(): Option[Int]


}

trait TreeMember[T <: ContentOrganizationThingy,ID]{

  /**
   * Gets the children, eg. in cas of folders the entries in it
   */
  def getChildren(t:T)(using i:Identifyable[T,ID]): Seq[ContentOrganizationThingy]


  /***
   * Gets the parent
   * ToDo:: Links ?
   */
  def getParent(t:T)(using i:Identifyable[T,ID]) : Option[ContentOrganizationThingy]

}

trait Identifyable[Object,Identifier]{
  def getID(i:Object) : Identifier
}

trait Connector[Identifier]{

  def getChildren(identifier: Identifier) : Seq[Identifier]

  def getParent(identifier: Identifier) : Identifier
}




final case class File(name:String,size: Int, parent :Directory ) extends ContentOrganizationThingy {

  override def displayName() = name

  override def displaySizeNumBytes(): Option[Int] = Some(size)

}


final case class Directory(name:String,parent:Option[Directory]) extends ContentOrganizationThingy {

  override def displayName() = name

  override def displaySizeNumBytes() = None

}

object ContentOrganizationThingy {
  val size = Lens[ContentOrganizationThingy,Option[Int]   ](_.displaySizeNumBytes())(x => {
    case f: File => f.copy(size = x.getOrElse(0))
    case d: Directory => d
  })
  val name = Lens[ContentOrganizationThingy , String](_.displayName())(x => {
    case f: File  => f.copy(name = x)
    case d: Directory => d.copy(name = x)
  })
}

val FileDisplay = ScalaComponent.builder[StateSnapshot[File]]
  .render_P { stateSnapshot =>
    <.span(
      ^.paddingLeft := "6ex", // leave some space for ReusabilityOverlay
      <.input.text(
        ^.value     := stateSnapshot.value
        //^.onChange ==> ((e: ReactEventFromInput) => stateSnapshot.setState(e.target.value))
      ))
  }
  .configure(ReusabilityOverlay.install)
  .build

object FileObjectComponent {

  type State = ContentOrganizationThingy

  final case class Props(c:Connector[String],ss: StateSnapshot[State]){
    @inline def render = Comp(c)(this)
  }


  given reuseabilityConn: Reusability[ui.Connector[String]] = Reusability.const(true)

  given reusabilityState: Reusability[State] = Reusability.derive

  given reusabilityProps: Reusability[Props] = Reusability.derive

  val Comp = (c:Connector[String] ) => ScalaComponent.builder[Props]
    .initialStateFromProps((p:Props) => p.ss.value )
    .backend((bs: BackendScope[Props, State]) => {

      given t:TreeMember[ContentOrganizationThingy,String] = new TreeMember[ContentOrganizationThingy,String] {

        def getChildren(t:ContentOrganizationThingy)(using i:Identifyable[ContentOrganizationThingy,String]): Seq[ContentOrganizationThingy] = t match {
          case File(name, size, parent) => Seq.empty
          case d: Directory => c.getChildren(i.getID(t)).map(n => Directory(n,Some(d)))
        }

        def getParent(t:ContentOrganizationThingy)(using i:Identifyable[ContentOrganizationThingy,String]) : Option[ContentOrganizationThingy] = None

      }

      given i:Identifyable[ContentOrganizationThingy,String] = (i: ContentOrganizationThingy) => {
        i.displayName()
      }

      Backend(bs)
    })
    .renderBackend
    .configure(Reusability.shouldComponentUpdate)
    .build

  final class Backend(bs: BackendScope[Props, State])(using t:TreeMember[ContentOrganizationThingy,String] )(using i:Identifyable[ContentOrganizationThingy,String]) {

    def render(p: Props): VdomElement = {
      <.ul(
        p.ss.value match {
          case f: File  => <.li(f.displayName())
          case d: Directory  => <.li(d.displayName() + t.getChildren(d).map(_.displayName()).mkString(""))
        }
      )
    }


  }

}


class FileTreeComponent(fileSystem:FileDescriptorStorage[Promise]) {

  type FTState = Directory // The Root Component
  type FTProps = Unit

  def getConnector(fileSystem:FileDescriptorStorage[Promise]) :Connector[String] = new Connector[String] {
    override def getChildren(identifier: String): Seq[String] = ???

    override def getParent(identifier: String): String = ???
  }

  final class Backend(bs: BackendScope[FTProps, FTState]) {
    private val setStateFn = StateSnapshot.withReuse.prepareVia(bs)

    def render(state :FTState): VdomElement = {
      given reuseabilityStateFT: Reusability[ContentOrganizationThingy] = Reusability.const(true)
      FileObjectComponent.Props(getConnector(fileSystem),japgolly.scalajs.react.extra.internal.StateSnapshot.withReuse.apply(state).readOnly).render
    }
  }

  def Component  = ScalaComponent.builder[Unit]
    .initialState(Directory("/",None))
    .renderBackend[Backend]
    .build
}








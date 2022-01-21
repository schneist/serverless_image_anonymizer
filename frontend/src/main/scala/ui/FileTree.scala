package ui

import scalajs.js
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import japgolly.scalajs.react.ReactCats.*
import japgolly.scalajs.react.extra._





sealed trait FileObject{
  def displayName:String
}

case class File(name:String) extends FileObject{
  override def displayName: String = name
}

case class Folder(name:String,files: Seq[FileObject]) extends FileObject{
  override def displayName: String = name
}

type State = Seq[FileObject]

lazy val TreeComponent = ScalaComponent.builder[State]
  .initialState(Seq.empty[FileObject])
  .renderBackend[Backend]
  .build


class Backend(bs: BackendScope[State, State]) {

  def render(s: State) : VdomElement=
    <.ul(
      s.toTagMod{
        case f: File => <.li(f.displayName)
        case d: Folder => <.li(d.displayName,TreeComponent(d.files))
      }
    )


}












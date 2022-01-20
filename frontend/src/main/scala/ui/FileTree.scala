package ui

import scalajs.js
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import japgolly.scalajs.react.ReactCats.*

import javax.tools.FileObject



object FileTree{

  sealed trait FileObject{
    def displayName:String
  }

  case class File(name:String) extends FileObject{
    override def displayName: String = name
  }

  case class Folder(name:String,files: List[FileObject]) extends FileObject{
    override def displayName: String = name
  }


  case class State(root: FileObject)

  class Backend($: BackendScope[Unit, State]) {




    def render(s: State) =
      ScalaComponent.builder[Unit]
      <.div(s.root.displayName)

  }


  def FileComponent = ScalaComponent.builder[Unit]
    .initialState(State(Folder("/",List.empty)))
    .renderBackend[Backend]
    .build

}
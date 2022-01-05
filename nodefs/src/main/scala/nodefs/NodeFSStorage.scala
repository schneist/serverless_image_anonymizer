package nodefs

import shared.FileDescriptorStorage
import typings.node.fsPromisesMod._
import typings.node.fsMod.PathLike
import typings.node.fsMod.Dirent
import scala.scalajs.js.Promise

abstract class NodeFSStorage extends  FileDescriptorStorage{

  override type path = PathLike
  type promise[_] = Promise[_]
  override type filedescriptor = FileHandle
  override type bytestream = String
  override type permissions = String
  override type userID = String
  override type groupID = String
  override type dirEnt = Dirent

  override def openR(p: path): promise[filedescriptor] = typings.node.fsPromisesMod.open(p,"r")
  override def openRW(p: path): promise[filedescriptor] = typings.node.fsPromisesMod.open(p,"r+")

  override def close(f: FileHandle): promise[Unit] = f.close()

  override def readDir(p: PathLike): Promise[scalajs.js.Array[Dirent]] = typings.node.fsPromisesMod.readdir(p, typings.node.anon.ObjectEncodingOptionswithEncoding.apply())
}


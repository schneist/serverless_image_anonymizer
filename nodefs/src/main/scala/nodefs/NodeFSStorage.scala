package nodefs

import shared.FileDescriptorStorage
import typings.node.bufferMod.global
import typings.node.fsPromisesMod.*
import typings.node.fsMod.PathLike

import scala.scalajs.js.Promise
import scala.scalajs.js.typedarray.Uint8Array

class NodeFSStorage extends  FileDescriptorStorage{

  override type path = PathLike
  override type promise[_] = Promise[_]
  override type filedescriptor = FileHandle
  override type bytestream = typings.node.bufferMod.global.Buffer
  override type permissions = String
  override type userID = String
  override type groupID = String
  override type dirEnt = typings.node.nodeFsMod.Dirent

  override def openR(p: path): promise[filedescriptor] = typings.node.fsPromisesMod.open(p,"r")
  override def openRW(p: path): promise[filedescriptor] = typings.node.fsPromisesMod.open(p,"r+")

  override def close(f: FileHandle): promise[Unit] = f.close()

  override def readDir(p: path): Promise[scalajs.js.Array[dirEnt]] = typings.node.fsPromisesMod.readdir(p, typings.node.anon.ObjectEncodingOptionswithEncoding.apply())

  override def rmdir(p:path): Promise[Unit] =  typings.node.fsPromisesMod.rmdir(p)

  override def mkdir(p: PathLike): Promise[Unit] = typings.node.fsPromisesMod.mkdir(p)

  override def read(f: FileHandle): Promise[bytestream] = f.readFile()

  override def write(f: FileHandle, content: global.Buffer): Promise[Unit] = f.writeFile(content.asInstanceOf[Uint8Array])

  override def link(f: FileHandle): Promise[_] = ???

  override def create(f: FileHandle): Promise[_] = ???

  override def unlink(f: FileHandle): Promise[_] = ???

  override def chmod(f: FileHandle, p: String): Promise[_] = ???

  override def chown(f: FileHandle, u: String, g: String): Promise[_] = ???
}


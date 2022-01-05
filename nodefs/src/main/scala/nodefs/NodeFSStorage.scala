package nodefs

import shared.FileDescriptorStorage
import typings.node.bufferMod.global
import typings.node.fsPromisesMod.*
import typings.node.fsMod.PathLike

import scala.scalajs.js.Promise
import scala.scalajs.js.typedarray.Uint8Array

object NodeFSStorage extends  FileDescriptorStorage{

  override type path = PathLike
  override type promise[_] = Promise[_]
  override type filedescriptor = FileHandle
  override type bytestream = typings.node.bufferMod.global.Buffer
  override type permissions = String
  override type userID = Double
  override type groupID = Double
  override type dirEnt = typings.node.nodeFsMod.Dirent

  override def openR(p: path): promise[filedescriptor] = typings.node.fsPromisesMod.open(p,"r")
  override def openRW(p: path): promise[filedescriptor] = typings.node.fsPromisesMod.open(p,"w+")

  override def close(f: filedescriptor): promise[Unit] = f.close()

  override def readDir(p: path): promise[scalajs.js.Array[dirEnt]] = typings.node.fsPromisesMod.readdir(p, typings.node.anon.ObjectEncodingOptionswithEncoding.apply())

  override def rmdir(p:path): promise[Unit] =  typings.node.fsPromisesMod.rmdir(p)

  override def mkdir(p: path): promise[Unit] = typings.node.fsPromisesMod.mkdir(p)

  override def read(f: filedescriptor): promise[bytestream] = f.readFile()

  override def write(f: filedescriptor, content: bytestream): Promise[Unit] = f.writeFile(content.asInstanceOf[Uint8Array])

  override def link(op: path,np :path): promise[Unit] = typings.node.fsPromisesMod.link(op,np)

  override def unlink(p:path): promise[_] = typings.node.fsPromisesMod.unlink(p)

  override def chmod(p:path, mode: permissions): promise[_] = typings.node.fsPromisesMod.chmod(p,mode)

  override def chown(p:path, u: userID, g: groupID): promise[_] = typings.node.fsPromisesMod.chown(p,u,g)
}


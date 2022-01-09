package nodefs

import shared.FileDescriptorStorage
import typings.node.bufferMod.global
import typings.node.fsPromisesMod.*
import typings.node.fsMod.PathLike

import scala.scalajs.js.Promise
import scala.scalajs.js.typedarray.Uint8Array

object NodeFSStorage extends  FileDescriptorStorage[Promise] {

  override type path = PathLike
  override type fileDescriptor = FileHandle
  override type buffer = typings.node.bufferMod.global.Buffer
  override type permissions = String
  override type userID = Double
  override type groupID = Double
  override type dirEnt = typings.node.nodeFsMod.Dirent

  override def openR(p: path) = typings.node.fsPromisesMod.open(p,"r")
  override def openRW(p: path) = typings.node.fsPromisesMod.open(p,"w+")

  override def close(f: FileHandle): Promise[Unit] = f.close()

  override def readDir(p: path): Promise[scalajs.js.Array[dirEnt]] = typings.node.fsPromisesMod.readdir(p, typings.node.anon.ObjectEncodingOptionswithEncoding.apply())

  override def rmdir(p:path): Promise[Unit] =  typings.node.fsPromisesMod.rmdir(p)

  override def mkdir(p: path) = typings.node.fsPromisesMod.mkdir(p)

  override def read(f: FileHandle) = f.readFile()

  override def write(f: FileHandle, content: buffer)= f.writeFile(content.asInstanceOf[Uint8Array])

  override def link(op: path,np :path) = typings.node.fsPromisesMod.link(op,np)

  override def unlink(p:path)= typings.node.fsPromisesMod.unlink(p)

  override def chmod(p:path, mode: permissions) = typings.node.fsPromisesMod.chmod(p,mode)

  override def chown(p:path, u: userID, g: groupID) = typings.node.fsPromisesMod.chown(p,u,g)
}


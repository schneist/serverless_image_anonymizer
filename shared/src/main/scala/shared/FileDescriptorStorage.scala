package shared

trait FileDescriptorStorage[Promise[_]] extends Storage {

  type path
  type fileDescriptor
  type buffer
  type permissions
  type userID
  type groupID
  type dirEnt

  def openR(p: path): Promise[fileDescriptor]

  def openRW(p: path): Promise[fileDescriptor]

  def close(f: fileDescriptor): Promise[Unit]

  def readDir(p: path): Promise[scalajs.js.Array[dirEnt]]

  def read(f: fileDescriptor): Promise[buffer]

  def write(f: fileDescriptor, content: buffer): Promise[Unit]

  def mkdir(p: path): Promise[Unit]

  def rmdir(p: path): Promise[Unit]

  def unlink(p:path): Promise[Unit]

  def link(existingPath:path,newPath:path): Promise[Unit]

  def chmod(p:path, mode: permissions): Promise[Unit]

  def chown(p:path, u: userID, g: groupID): Promise[Unit]


}

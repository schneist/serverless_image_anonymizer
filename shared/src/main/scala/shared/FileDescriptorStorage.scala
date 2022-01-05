package shared

trait FileDescriptorStorage extends Storage {

  type path
  type promise[_]
  type filedescriptor
  type bytestream
  type permissions
  type userID
  type groupID
  type dirEnt

  def openR(p: path): promise[filedescriptor]

  def openRW(p: path): promise[filedescriptor]

  def close(f: filedescriptor): promise[Unit]

  def readDir(p: path): promise[Seq[dirEnt]]

  def read(f: filedescriptor): promise[bytestream]

  def write(f: filedescriptor, content: bytestream): promise[Unit]

  def mkdir(p: path): promise[Unit]

  def rmdir(p: path): promise[Unit]

  def create(f: filedescriptor): promise[Unit]

  def unlink(f: filedescriptor): promise[Unit]

  def link(f: filedescriptor): promise[Unit]

  def chmod(f: filedescriptor, p: permissions): promise[Unit]

  def chown(f: filedescriptor, u: userID, g: groupID): promise[Unit]


}

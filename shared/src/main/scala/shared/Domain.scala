package shared

sealed trait Domain

sealed trait Image extends Domain

sealed trait Action extends Domain

sealed trait Collection extends Domain

sealed trait Storage extends Domain


sealed trait FileDescriptorStorage extends Storage {


  type path
  type promise[_]
  type filedescriptor
  type bytestream
  type permissions
  type userID
  type groupID

  def open(p:path): filedescriptor
  def close(p:path): filedescriptor

  def read(f:filedescriptor) : promise[bytestream]
  def write(f:filedescriptor,content: bytestream) : promise[Unit]

  def mkdir(f:filedescriptor):promise[Unit]
  def rmdir(f:filedescriptor):promise[Unit]

  def create(f:filedescriptor):promise[Unit]

  def unlink(f:filedescriptor):promise[Unit]
  def link(f:filedescriptor):promise[Unit]

  def chmod(f:filedescriptor,p:permissions):promise[Unit]
  def chown(f:filedescriptor,u:userID,g:groupID):promise[Unit]


}
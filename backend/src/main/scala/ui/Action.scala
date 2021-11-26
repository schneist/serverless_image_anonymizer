package ui

import cats.MonadError
import cats.data.EitherT
import cats.effect.IO
import typings.node.bufferMod.global.Buffer
import typings.tensorflowTfjsCore.distTensorMod.{Tensor3D, Tensor4D}

import java.io.FileNotFoundException
import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js.typedarray.Uint8Array
import scala.util.{Failure, Success, Try}

trait Action[Environment, Input, Output, ActionError] {

  def execute(i: Input)(using environment: Environment, me: MonadError[IO, ActionError]): EitherT[IO, ActionError, Output]

}
sealed trait LoadImageErrors
class FileNotFound extends LoadImageErrors

object LoadImageAction extends Action[EnvironmentWithExecutionContext,String,Uint8Array,LoadImageErrors]{

  def execute(filepath: String)
             (using environment: EnvironmentWithExecutionContext,
              me: cats.MonadError[cats.effect.IO, LoadImageErrors]
             ):cats.data.EitherT[cats.effect.IO, LoadImageErrors, Uint8Array] = {
    implicit val ec = environment.getExecutionContext()
    for {
      image ← EitherT(
        IO.fromFuture(IO(
          typings.node.fsPromisesMod.readFile(filepath).toFuture.map(a ⇒ {println("################################");a.asInstanceOf[Uint8Array]}).transform { a ⇒
            println("################################")
            a match {
              case f: Failure[Throwable] ⇒ Try(Left(FileNotFound()))
              case s: Success[Buffer] ⇒ Try(Right(s))
            }
          }
        ))
      )
    } yield image.get
  }

  def checkFileAccess(filepath: String)
                     (using environment: EnvironmentWithExecutionContext,
                      me: cats.MonadError[cats.effect.IO, LoadImageErrors]
                     ):cats.data.EitherT[cats.effect.IO, LoadImageErrors, Unit] = {
    implicit val ec = environment.getExecutionContext()
    EitherT {
      IO.fromFuture(IO(
        typings.node.fsPromisesMod.access(filepath).toFuture.transform {
          _ match {
            case f: Failure[Unit] ⇒ Try(Left(FileNotFound()))
            case s: Success[Unit] ⇒ Try(Right(s))
          }
        }
      ))
    }
  }
}

trait Environment

trait EnvironmentWithExecutionContext extends Environment{
  def getExecutionContext():ExecutionContext
}

abstract class ExtractImageData extends Action[Unit,Uint8Array,Tensor3D | Tensor4D ,Throwable]{

}
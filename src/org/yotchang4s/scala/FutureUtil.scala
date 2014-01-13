package org.yotchang4s.scala

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.Promise
import scala.actors.threadpool.CancellationException

object FutureUtil {
  def cancellableFuture[T](fun: Future[T] => T)(implicit ex: ExecutionContext): (Future[T], () => Boolean) = {
    val p = Promise[T]()
    val f = p.future
    p tryCompleteWith Future(fun(f))
    (f, () => p.tryFailure(new CancellationException))
  }
}
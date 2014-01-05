package org.yotchang4s.pixiv

import PixivException._

object PixivException {
  sealed trait ErrorType
  case object ApplicationError extends ErrorType
  case object IOError extends ErrorType
  case object UnknownError extends ErrorType
  case object NoImplements extends ErrorType
}

@serializable
@SerialVersionUID(1L)
class PixivException(val errorType: ErrorType, message: Option[String] = None, cause: Option[Throwable] = None)
  extends Exception(message getOrElse null, cause getOrElse null) {

  def this(errorType: ErrorType) = this(UnknownError, None, None)
  def this(errorType: ErrorType, cause: Option[Throwable]) = this(UnknownError, None, cause)
}

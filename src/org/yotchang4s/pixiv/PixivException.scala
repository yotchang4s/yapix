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
class PixivException(val errorType: PixivException.ErrorType, message: String = null, cause: Throwable = null)
  extends Exception(message, cause) {

  def this(errorType: ErrorType, cause: Throwable) = this(UnknownError, null, cause)
}

package org.yotchang4s.pixiv

@serializable
@SerialVersionUID(1L)
class PixivException(_message: Option[String] = None, _cause: Option[Throwable] = None)
  extends Exception(_message getOrElse null, _cause getOrElse null) {

  def message = _message
  def cause = _cause
  
  def this() = this(None, None)
  def this(message: String) = this(Some(message), None)
  def this(cause: Throwable) = this(None, Some(cause))
}

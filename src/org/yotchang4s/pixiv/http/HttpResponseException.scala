package org.yotchang4s.pixiv.http

import org.yotchang4s.pixiv.PixivException

@serializable
@SerialVersionUID(1L)
class HttpResponseException(val httpResponse: HttpResponse, message: Option[String] = None, cause: Option[Throwable] = None)
  extends PixivException(PixivException.IOError, message, cause) {

  def this(httpResponse: HttpResponse) = this(httpResponse, None, None)
  def this(httpResponse: HttpResponse, cause: Option[Throwable]) = this(httpResponse, None, cause)
}

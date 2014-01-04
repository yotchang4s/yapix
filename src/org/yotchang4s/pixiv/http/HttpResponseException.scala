package org.yotchang4s.pixiv.http

import org.yotchang4s.pixiv.PixivException

@serializable
@SerialVersionUID(1L)
class HttpResponseException(_httpResponse: HttpResponse, message: Option[String] = None, cause: Option[Throwable] = None)
  extends PixivException(message, cause) {

  def httpResponse = _httpResponse;

  def this(httpResponse: HttpResponse) = this(httpResponse, None, None)
  def this(httpResponse: HttpResponse, message: String) = this(httpResponse, Some(message), None)
}

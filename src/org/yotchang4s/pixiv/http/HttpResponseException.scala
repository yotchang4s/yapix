package org.yotchang4s.pixiv.http

import org.yotchang4s.pixiv.PixivException

@serializable
@SerialVersionUID(1L)
class HttpResponseException(val httpResponse: HttpResponse, message: String = null, cause: Throwable = null)
  extends PixivException(PixivException.IOError, message, cause) {

  def this(httpResponse: HttpResponse, cause: Throwable) = this(httpResponse, null, cause)
}

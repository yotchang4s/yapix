package org.yotchang4s.pixiv

import org.jsoup.nodes._
import org.yotchang4s.pixiv.auth._
import org.yotchang4s.pixiv.http._
import org.yotchang4s.pixiv.PixivException._

private[pixiv] abstract class AbstractResourceDelegator {

  @throws(classOf[HttpResponseException])
  protected def checkHttpStatusCodeOk(httpResponse: HttpResponse) {
    val statusCode = httpResponse.responseStatusCode
    if (statusCode != 200) {
      throw new HttpResponseException(httpResponse,
        Some("Http status code is not 200 [" + statusCode + "]"))
    }
  }

  @throws(classOf[PixivException])
  protected def checkAuthentication(document: Document) {
    if (document.select(".newindex-signin").first != null) {
      throw new PixivException(IOError, Some("No authentication"))
    }
  }

  @throws(classOf[PixivException])
  protected def createAuthHttpRequestParameters(implicit config: Config): Map[String, String] = {
    config.authToken match {
      case Some(t) => Map("PHPSESSID" -> t)
      case None => throw new PixivException(IOError, Some("User access token is not found"))
    }
  }
}
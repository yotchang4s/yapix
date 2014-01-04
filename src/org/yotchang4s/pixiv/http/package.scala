package org.yotchang4s.pixiv

import java.net.URLEncoder
import java.io.UnsupportedEncodingException
import scala.reflect.ClassTag

package object http {

  implicit def tuple2RequestParameter(parameter: (String, String)): HttpRequestParameter =
    new HttpRequestParameter(parameter._1, parameter._2)

  implicit def tuple2RequestHeader(parameter: (String, String)): HttpRequestHeader =
    new HttpRequestHeader(parameter._1, parameter._2)

  @throws(classOf[UnsupportedEncodingException])
  implicit class HttpRequestParametersOpts(requestParams: List[HttpRequestParameter]) {
    def asUrlQueryString(enc: String): String = {
      requestParams.map { param =>
        URLEncoder.encode(param.key, enc) + "=" + URLEncoder.encode(param.value, enc)
      }.mkString("&")
    }
  }
}
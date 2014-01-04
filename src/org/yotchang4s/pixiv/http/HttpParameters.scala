package org.yotchang4s.pixiv.http

trait HttpParameter {
  val key: String
  val value: String
  lazy val keyAndVakue: (String, String) = (key, value)
}

case class HttpCookie(key: String, value: String, attributes: (String, String)*) extends HttpParameter {
  def this(keyAndVakue: (String, String), attributes: (String, String)*) {
    this(keyAndVakue._1, keyAndVakue._2, attributes: _*)
  }
}
case class HttpRequestParameter(key: String, value: String) extends HttpParameter

case class HttpRequestHeader(key: String, value: String) extends HttpParameter
case class HttpResponseHeader(key: String, value: String) extends HttpParameter

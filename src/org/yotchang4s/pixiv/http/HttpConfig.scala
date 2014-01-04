package org.yotchang4s.pixiv.http

object HttpConfig extends HttpConfig {
  def httpProxyHost: Option[String] = None
  def httpProxyPort: Option[Int] = None
  def httpProxyUser: Option[String] = None
  def httpProxyPassword: Option[String] = None
}

trait HttpConfig {
  def httpProxyHost: Option[String]
  def httpProxyPort: Option[Int]
  def httpProxyUser: Option[String]
  def httpProxyPassword: Option[String]
}

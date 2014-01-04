package org.yotchang4s.pixiv

import org.yotchang4s.pixiv.auth._
import org.yotchang4s.pixiv.http._

trait Config extends HttpConfig {
  def authToken: Option[String]

  def pixivId: Option[String]
  def pixivPassword: Option[String]
}

package org.yotchang4s.pixiv.auth;

import org.yotchang4s.pixiv.PixivException

sealed case class AuthUser(pixivId: String, pixivPassword: String)

sealed trait AuthResult {
  def isSuccess: Boolean
  final def isFailure: Boolean = !isSuccess
}

case class AuthSuccess(pixivId: String, userId: String, authToken: String) extends AuthResult {
  def isSuccess = true
}

case class AuthFailure(reasonMessage: String, cause: Option[PixivException] = None) extends AuthResult {
  def isSuccess = false
}
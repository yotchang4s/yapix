package org.yotchang4s.pixiv.auth;

import org.yotchang4s.pixiv.PixivException

sealed case class AuthUser(pixivId: String, pixivPassword: String)

sealed trait AuthResult {
  def isSuccess: Boolean
  final def isFailure: Boolean = !isSuccess
}

case class AuthSuccess(
  pixivId: String,
  userId: String,
  pivixPassword: String,
  authToken: String) extends AuthResult {

  def isSuccess = true
}

case class AuthFailure(reasonMessage: String, cause: Option[PixivException] = None) extends AuthResult {
  def this(cause: Option[PixivException]) {
    this(null, cause)
  }

  def isSuccess = false
}
package org.yotchang4s.pixiv.auth

import java.io.IOException
import java.util.regex._
import org.yotchang4s.pixiv._
import org.yotchang4s.pixiv.auth._
import org.yotchang4s.pixiv.http._
import scala.collection.convert.WrapAsScala._
import org.yotchang4s.scala.Loan
import org.yotchang4s.scala.Loan._

object FormAuth {
  private val authPostUrl = "http://www.pixiv.net/login.php"
  private val idPattern = Pattern.compile("^([0-9]+)_[a-zA-Z0-9]+$")
}

case class FormAuth {

  import FormAuth._

  def authcation(pixivId: String, pixivPassword: String)(implicit config: Config): AuthResult = {

    val params: List[HttpRequestParameter] = List(
      "mode" -> "login",
      "return_to" -> "/",
      "pixiv_id" -> pixivId,
      "pass" -> pixivPassword,
      "skip" -> "1")

    try {
      val http = new Http
      http.userAgent("Yapix")

      for (response <- Loan(http.post(authPostUrl, Some(params)))) {

        if (response.responseStatusCode != 302) {
          return AuthFailure("Http status code is not 302 [" + response.responseStatusCode + "]")
        }

        response.cookies.get("PHPSESSID") match {
          case Some(session) =>
            val sessionCookieValue = session.last.value
            val userId = {
              val matcher = idPattern.matcher(sessionCookieValue)
              if (!matcher.find) {
                return AuthFailure("User id is not found:")
              }
              matcher.group(1)
            }

            AuthSuccess(pixivId, userId, pixivPassword, sessionCookieValue)

          case None => AuthFailure("Login PHPSESSID is not found")
        }
      }
    } catch {
      case e: IOException => AuthFailure("IO Error",
        Some(new PixivException(PixivException.IOError, e)))
      case e: Exception => AuthFailure("Unknown Error",
        Some(new PixivException(PixivException.UnknownError, e)))
    }
  }
}

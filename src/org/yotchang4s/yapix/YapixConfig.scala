package org.yotchang4s.yapix

import org.yotchang4s.pixiv.Config
import org.yotchang4s.pixiv.auth.AuthResult
import android.content.SharedPreferences

object YapixConfig {
  private val httpProxyHostKey = "httpProxyHostKey"
  private val httpProxyPortKey = "httpProxyPort"
  private val httpProxyUserKey = "httpProxyUser"
  private val httpProxyPasswordKey = "httpProxyPassword"

  private val pixivIdKey = "pixivIdKey"
  private val pixivPasswordKey = "pixivPasswordKey"

  private val userIdKey = "userIdKey"

  var _yapixConfig: YapixConfig = null

  def sharedPreferences(sharedPreferences: SharedPreferences) {
    _yapixConfig = YapixConfig(sharedPreferences)
  }

  implicit def yapixConfig: YapixConfig = _yapixConfig
}

case class YapixConfig(sharedPreferences: SharedPreferences) extends Config {

  import YapixConfig._

  var _authToken: Option[String] = None

  def authToken: Option[String] = _authToken
  def authToken(authToken: String) { _authToken = Option(authToken) }

  def pixivId: Option[String] = getString(pixivIdKey)
  def pixivId(pixivId: String) { putString(pixivIdKey, pixivId) }

  def pixivPassword: Option[String] = getString(pixivPasswordKey)
  def pixivPassword(pixivPassword: String) { putString(pixivPasswordKey, pixivPassword) }

  def userId: Option[String] = getString(userIdKey)
  def userId(userId: String) { putString(userIdKey, userId) }

  def httpProxyHost: Option[String] = getString(httpProxyHostKey)
  def httpProxyHost(httpProxyHost: String) { putString(httpProxyHostKey, httpProxyHost) }

  def httpProxyPort: Option[Int] = {
    sharedPreferences.getInt(httpProxyPortKey, -1) match {
      case x if (x < 0) => None
      case x => Some(x)
    }
  }

  def httpProxyPort(httpProxyPort: Int) {
    val editor = sharedPreferences.edit
    if (httpProxyPort < 0) {
      editor.remove(httpProxyPortKey)
    } else {
      editor.putInt(httpProxyPortKey, httpProxyPort)
    }
    editor.commit
  }

  def httpProxyUser: Option[String] = getString(httpProxyUserKey)
  def httpProxyUser(httpProxyUser: String) { putString(httpProxyUserKey, httpProxyUser) }

  def httpProxyPassword: Option[String] = getString(httpProxyPasswordKey)
  def httpProxyPassword(httpProxyPassword: String) { putString(httpProxyPasswordKey, httpProxyPassword) }

  private def putString(key: String, value: String) {
    val editor = sharedPreferences.edit
    if (value == null) {
      editor.remove(key)
    } else {
      editor.putString(key, value)
    }
    editor.commit
  }

  private def getString(key: String): Option[String] = {
    Option(sharedPreferences.getString(key, null))
  }
}
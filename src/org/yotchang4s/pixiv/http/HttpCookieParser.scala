package org.yotchang4s.pixiv.http

object HttpCookieParser {

  def parse(cookie: String): Option[HttpCookie] = {
    // TODO 最後に;が無い場合は？
    val cookieElements = cookie.split(";")
    if (cookieElements.length == 0) return None

    val keyAndValue = getKeyAndValue(cookieElements.head) match {
      case Some(kv) => kv
      case None => return None
    }
    val attrs = cookieElements.map(getKeyAndValue).filter {
      case Some(_) => true
      case None => false
    } map {
      case Some(kv) => kv
      case None => return None
    }

    Some(new HttpCookie(keyAndValue, attrs: _*))
  }

  private def getKeyAndValue(element: String): Option[(String, String)] = {
    val equalIndex = element.indexOf("=")
    if (equalIndex == -1 || equalIndex == 0) {
      return None
    }
    val key = element.substring(0, equalIndex).trim
    val value = element.substring(equalIndex + 1).trim

    return Some(key, value)
  }
}

package org.yotchang4s.pixiv.http

import java.io._
import java.net._
import org.yotchang4s.scala.Loan
import org.yotchang4s.scala.Loan._

object RequestMethod {
  case object Get extends RequestMethod("GET")
  case object Post extends RequestMethod("POST")
}

sealed abstract class RequestMethod(_name: String) {
  def name = _name
}

class Http {

  private[this] var _userAgent: Option[String] = None
  private[this] var _referrer: Option[String] = None

  def userAgent(userAgent: String) { _userAgent = Option(userAgent) }
  def userAgent: Option[String] = _userAgent

  def referrer(referrer: String) { _referrer = Option(referrer) }
  def referrer: Option[String] = _referrer

  @throws(classOf[IOException])
  def get(url: String, params: Option[List[HttpRequestParameter]] = None,
    headers: Option[List[HttpRequestHeader]] = None,
    cookies: Option[List[HttpCookie]] = None)(implicit httpConfig: HttpConfig): HttpResponse =
    request(url, RequestMethod.Get, params, headers, cookies)(httpConfig)

  @throws(classOf[IOException])
  def post(url: String,
    params: Option[List[HttpRequestParameter]] = None,
    headers: Option[List[HttpRequestHeader]] = None,
    cookies: Option[List[HttpCookie]] = None)(implicit httpConfig: HttpConfig): HttpResponse =
    request(url, RequestMethod.Post, params, headers, cookies)(httpConfig)

  @throws(classOf[IOException])
  def request(url: String, method: RequestMethod,
    params: Option[List[HttpRequestParameter]] = None,
    headers: Option[List[HttpRequestHeader]] = None,
    cookies: Option[List[HttpCookie]] = None)(implicit httpConfig: HttpConfig): HttpResponse = {

    val paramsData = params.map(_.asUrlQueryString("UTF-8")).getOrElse("")

    val connectUrl =
      if (method == RequestMethod.Get && !params.isEmpty) {
        url + '?' + paramsData
      } else url

    val con = getConnection(connectUrl)(httpConfig)
    con.setRequestMethod(method.name)
    con.setInstanceFollowRedirects(false)

    if (method == RequestMethod.Post) {
      if (!"".equals(paramsData)) {
        con.addRequestProperty("Content-Type", "application/x-www-form-urlencoded")
        con.setDoOutput(true)
        con.setFixedLengthStreamingMode(paramsData.length)
      }
    }

    userAgent.foreach(con.setRequestProperty("User-Agent", _))
    referrer.foreach(con.setRequestProperty("Referrer", _))

    cookies.foreach { cs =>
      val cookieString = cs.map { cookie =>
        URLEncoder.encode(cookie.key) + "=" + URLEncoder.encode(cookie.value)
      }.mkString("; ")

      con.setRequestProperty("Cookie", cookieString)
    }
    headers.getOrElse(Nil).foreach { header =>
      con.setRequestProperty(header.key, header.value)
    }

    import scala.collection.convert.WrapAsScala._
    val requestProperties = con.getRequestProperties.toMap.map { case (k, v) => (k, v.toList) }

    con.connect

    if (method == RequestMethod.Post && !paramsData.isEmpty()) {
      for {
        writer <- Loan(new BufferedWriter(new OutputStreamWriter(con.getOutputStream())))
      } {
        writer.write(paramsData)
      }
    }

    new HttpResponse(con, con.getResponseCode, requestProperties)
  }

  @throws(classOf[IOException])
  private def getConnection(url: String)(implicit httpConfig: HttpConfig): HttpURLConnection = {
    val u = new URL(url)

    for {
      proxyUser <- httpConfig.httpProxyUser
      proxyPassword <- httpConfig.httpProxyPassword
    } {
      Authenticator.setDefault(new Authenticator {
        protected override def getPasswordAuthentication: PasswordAuthentication = {
          if (getRequestorType == Authenticator.RequestorType.PROXY) {
            new PasswordAuthentication(proxyUser, proxyPassword.toCharArray)
          } else null
        }
      })
    }

    for {
      proxyHost <- httpConfig.httpProxyHost
      proxyPort <- httpConfig.httpProxyPort
    } {
      val proxy = new Proxy(
        Proxy.Type.HTTP,
        InetSocketAddress.createUnresolved(proxyHost, proxyPort))

      return u.openConnection(proxy).asInstanceOf[HttpURLConnection]
    }

    u.openConnection.asInstanceOf[HttpURLConnection]
  }

  protected def isProxyConfigured(implicit httpConfig: HttpConfig): Boolean = {
    return !httpConfig.httpProxyHost.isEmpty
  }

  protected def isProxyAuthConfigured(implicit httpConfig: HttpConfig): Boolean = {
    return !httpConfig.httpProxyUser.isEmpty
  }
}
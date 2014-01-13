package org.yotchang4s.pixiv.http

import java.io._
import java.net._
import scala.collection._
import org.yotchang4s.scala.Loan
import org.yotchang4s.scala.Loan._
import scala.io._
import scala.collection.convert.WrapAsScala._
import java.nio.charset.Charset
import java.io.Closeable

class HttpResponse(con: HttpURLConnection, statusCode: Int,
  requestProperties: Map[String, List[String]]) extends Closeable {
  var cacheCookies = immutable.Map[String, List[HttpCookie]]()

  var in: InputStream = new HttpURLConnectionCloseableInputStream(con,
    Option(con.getErrorStream) getOrElse con.getInputStream)

  def requestHeaders: Map[String, List[String]] = this.requestProperties

  def responseStatusCode: Int = statusCode

  def responseHeaders: immutable.Map[String, immutable.List[String]] =
    con.getHeaderFields.toMap.map { case (k, v) => (k, v.to[List]) }

  def cookies: immutable.Map[String, immutable.List[HttpCookie]] = {
    if (cacheCookies.isEmpty) {
      cacheCookies = createCookie
    }
    this.cacheCookies
  }

  private def createCookie: immutable.Map[String, List[HttpCookie]] = {
    val rawCookies = responseHeaders.get("Set-Cookie")
    val cookies = rawCookies match {
      case Some(cookies) => cookies.flatMap(HttpCookieParser.parse)
      case None => Nil
    }
    cookies.groupBy(_.key)
  }

  def responseHeaderFirst(key: String): Option[String] =
    responseHeader(key) match {
      case Nil => None
      case x => Some(x.head)
    }

  def responseHeader(key: String): immutable.List[String] =
    Option(con.getHeaderFields.get(key)) match {
      case Some(value) => value.to[List]
      case None => Nil
    }

  def asStream: InputStream = in
  def asReader: Reader = asReader(Charset.defaultCharset)
  def asReader(charsetName: String): Reader = asReader(Charset.forName(charsetName))
  def asReader(charset: Charset): Reader = new InputStreamReader(asStream, charset)

  def asString: String = asString(Charset.defaultCharset)
  def asString(charsetName: String): String = asString(Charset.forName(charsetName))
  def asString(charset: Charset): String = {
    for {
      reader <- Loan(new BufferedReader(asReader(charset)))
    } {
      val buffer = new Array[Char](4096);
      val builder = new StringBuilder

      Stream.continually(reader.read(buffer)).takeWhile(-1 !=).foreach {
        builder.appendAll(buffer, 0, _)
      }

      builder.toString;
    }
  }

  def close = in.close

  private class HttpURLConnectionCloseableInputStream(conn: HttpURLConnection, in: InputStream)
    extends FilterInputStream(in) {

    @throws(classOf[IOException])
    override def close {
      try {
        super.close
      } finally {
        this.conn.disconnect
      }
    }
  }
}
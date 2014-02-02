package org.yotchang4s.pixiv.bookmark

import java.io.BufferedReader
import java.io.IOException
import java.io.Reader

import scala.collection._
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import org.jsoup.nodes.Element
import org.yotchang4s.scala.Loan
import org.yotchang4s.scala.Loan._
import org.yotchang4s.pixiv._
import org.yotchang4s.pixiv.http._
import org.yotchang4s.pixiv.illust._
import org.yotchang4s.pixiv.PixivException._
import BookmarkComponent._

private[pixiv] trait BookmarkComponentImpl extends BookmarkComponent { this: IllustComponent =>
  val bookmark: BookmarkRepository

  val illustIdPattern = """.*&illust_id=([0-9]+)$""".r

  val bookmarkUrlBase = "http://spapi.pixiv.net/iphone/bookmark.php?"

  class BookmarkRepositoryImpl extends BookmarkRepository {
    def findSelfBookmarks(privacyType: PrivacyType, page: Int)(implicit config: Config): Either[PixivException, List[IllustDetail]] = {
      val http = new Http
      http.userAgent("Yapix")

      val bookmarkUrl = {
        val bu = privacyType match {
          case Open => bookmarkUrlBase + "rest=show"
          case Close => bookmarkUrlBase + "rest=hide"
        }
        bu + config.authToken.map("&PHPSESSID=" + _).getOrElse("")
      }

      val response =
        try {
          http.get(bookmarkUrl + "&p=" + page, None, None, None)
        } catch {
          case e: IOException => return Left(new PixivException(IOError, e))
        }
      try {
        val rankings = IllustDetailCsvParser.parseIllusts(response.asReader("UTF-8"))

        Right(rankings)

      } catch {
        case e: IOException => Left(new HttpResponseException(response, e))
        case e: Exception => Left(new PixivException(UnknownError, e))
      }
    }
  }
}
package org.yotchang4s.pixiv.bookmark

import org.yotchang4s.pixiv.Config
import org.yotchang4s.pixiv.illust.Illust
import org.yotchang4s.pixiv.PixivException
import org.yotchang4s.pixiv.PixivException._
import org.yotchang4s.pixiv.http._
import org.yotchang4s.pixiv.bookmark.BookmarkComponent._
import org.jsoup.Jsoup
import org.yotchang4s.pixiv.illust.IllustId
import java.io.IOException
import org.yotchang4s.scala.Loan
import org.yotchang4s.scala.Loan._
import org.jsoup.nodes.Element
import scala.collection.mutable.ListBuffer
import org.yotchang4s.pixiv.illust.IllustComponent
import org.jsoup.select.Elements
import java.io.BufferedReader
import java.io.Reader
import au.com.bytecode.opencsv.CSVParser
import org.yotchang4s.pixiv.illust.IllustDetail
import org.yotchang4s.pixiv.illust.IllustDetailCsvParser

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
          case e: IOException => return Left(new PixivException(IOError, Some(e)))
        }
      for (r <- Loan(response)) {
        try {
          val reader = response.asReader("UTF-8")

          val rankings = toBookmarkIllusts(reader)

          Right(rankings)

        } catch {
          case e: IOException => Left(new HttpResponseException(response, Some(e)))
          case e: Exception => Left(new PixivException(UnknownError, Some(e)))
        }
      }
    }
  }

  private def toBookmarkIllusts(reader: Reader): List[IllustDetail] = {
    val r = new BufferedReader(reader)
    Stream.continually(r.readLine).takeWhile(null !=).map { l =>
      IllustDetailCsvParser.parse(l)
    }.toList
  }
}
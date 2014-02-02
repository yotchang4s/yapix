package org.yotchang4s.pixiv.search

import org.yotchang4s.pixiv.Config
import org.yotchang4s.pixiv.PixivException
import org.yotchang4s.pixiv.illust.IllustDetail
import org.yotchang4s.pixiv.illust.IllustDetailCsvParser
import org.yotchang4s.pixiv.tag.Tag
import java.net.URLEncoder
import org.yotchang4s.pixiv.http.Http
import org.yotchang4s.scala.Loan
import org.yotchang4s.scala.Loan._
import java.io.IOException
import org.yotchang4s.pixiv.http.HttpResponseException
import org.yotchang4s.pixiv.PixivException._

private[pixiv] trait SearchComponentImpl extends SearchComponent {
  private val urlBase = "http://spapi.pixiv.net/iphone/search.php?"
  private val tagSearchIrlBase = urlBase + "s_mode=s_tag"

  class SearchRepositoryImpl extends SearchRepository {

    def searchTag(tag: Tag, page: Int)(implicit config: Config): Either[PixivException, List[IllustDetail]] = {
      val searchTagUrl = {
        val iu = tagSearchIrlBase + "&word=" + URLEncoder.encode(tag.identity.value) + "&p" + page
        config.authToken match {
          case Some(a) => iu + "&PHPSESSID=" + a
          case None => iu
        }
      }

      val http = new Http
      http.userAgent("Yapix")

      val response =
        try {
          http.get(searchTagUrl)
        } catch {
          case e: IOException => return Left(new PixivException(IOError, e))
        }
      try {
        val illusts = IllustDetailCsvParser.parseIllusts(response.asReader("UTF-8"))

        Right(illusts)

      } catch {
        case e: IOException => Left(new HttpResponseException(response, e))
        case e: Exception => Left(new PixivException(UnknownError, e))
      }
    }
  }
}
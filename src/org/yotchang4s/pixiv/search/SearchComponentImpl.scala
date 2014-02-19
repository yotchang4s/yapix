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
import org.yotchang4s.pixiv.illust.Caption

private[this] object SearchComponentImpl {
  sealed trait SearchType
  case object TagSearch extends SearchType
  case object CaptionSearch extends SearchType
}

private[pixiv] trait SearchComponentImpl extends SearchComponent {
  private val baseUrl = "http://spapi.pixiv.net/iphone/search.php?order=date_d"

  private val tagSearchUrlParts = "&s_mode=s_tag"
  private val captionSearchUrlParts = "&s_mode=s_tc"

  class SearchRepositoryImpl extends SearchRepository {

    import SearchComponentImpl._

    def search(tag: Tag, page: Int)(implicit config: Config): Either[PixivException, List[IllustDetail]] = {
      search(TagSearch, tag.identity.value, page)(config)
    }

    def search(caption: Caption, page: Int)(implicit config: Config): Either[PixivException, List[IllustDetail]] = {
      search(CaptionSearch, caption.value, page)(config)
    }

    def search(searchType: SearchType, word: String, page: Int)(implicit config: Config): Either[PixivException, List[IllustDetail]] = {

      val http = new Http
      http.userAgent("Yapix")

      val response =
        try {
          http.get(getSearchUrl(searchType, word, page)(config))
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

    private def getSearchUrl(searchType: SearchType, word: String, page: Int)(implicit config: Config) = {
      val urlWordAndPageUrlParts = "&word=" + URLEncoder.encode(word) + "&p" + page
      val urlAuthTokenUrlParts = config.authToken match {
        case Some(a) => "&PHPSESSID=" + a
        case None => ""
      }
      val urlModeSuffix = {
        searchType match {
          case TagSearch => tagSearchUrlParts
          case CaptionSearch => captionSearchUrlParts
        }
      }

      val url = baseUrl + urlWordAndPageUrlParts + urlAuthTokenUrlParts + urlModeSuffix

      url
    }
  }
}
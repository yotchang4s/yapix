package org.yotchang4s.pixiv.illust

import org.yotchang4s.pixiv.http._
import org.yotchang4s.pixiv.tag._
import org.yotchang4s.pixiv._
import org.jsoup.Jsoup
import scala.util.Try
import scala.util.Success
import scala.util.Failure
import scala.util.Either
import org.jsoup.nodes.Document

private[pixiv] trait IllustComponentImpl extends IllustComponent {
  private val illustUrlBase = "http://www.pixiv.net/member_illust.php?mode=medium&illust_id="

  class IllustRepositoryImpl extends IllustRepository {
    def findIllustBy(id: IllustId)(implicit config: Config): Either[PixivException, Illust] = {
      findIllustDetailBy(id)(config)
    }

    def findIllustDetailBy(id: IllustId)(implicit config: Config): Either[PixivException, IllustDetail] = {
      val http = new Http
      http.userAgent("Yapix")

      val response = http.get(illustUrlBase + id.value)
      val responseString = response.asString

      val document = Jsoup.parse(responseString)
      val widthAndHeight = getWidthAndHeight(document) match {
        case Left(x) => return Left(x)
        case Right(x) => x
      }

      Right(new IllustDetail {
        private val _detail: IllustDetail = this

        val illust = new IllustImpl {
          val identity = id
          val title = ""
          val width = widthAndHeight._1
          val height = widthAndHeight._1
          val tags = List[Tag]()
          val url = ""

          def detail(implicit config: Config): Either[PixivException, IllustDetail] = Right(_detail)
        }

        def detail(implicit config: Config) = Right(this)
      })
    }

    private def getWidthAndHeight(document: Document): Either[PixivException, (Int, Int)] = {
      val widthAndHeightRegex = """^(.+)×(.+)$""".r
      val size: (Int, Int) = document.select("ul.meta > li:nth-of-type(2)") match {
        case widthAndHeightRegex(w, h) =>
          try {
            (w.toInt, h.toInt)
          } catch {
            case e: NumberFormatException =>
              return Left(new PixivException(PixivException.IOError, Some("width or height is not number")))
          }
        case _ =>
          return Left(new PixivException(PixivException.IOError, Some("width and height not found")))
      }
      Right(size)
    }
  }
}

private[this] trait IllustImpl extends Illust {

}
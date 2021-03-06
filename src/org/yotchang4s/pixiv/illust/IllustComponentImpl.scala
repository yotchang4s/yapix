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
import org.yotchang4s.pixiv.PixivException._
import org.yotchang4s.pixiv.user.User
import java.io.BufferedReader
import java.io.IOException
import java.util.Date

private[pixiv] trait IllustComponentImpl extends IllustComponent {
  private val illustUrlBase = "http://spapi.pixiv.net/iphone/illust.php?"

  class IllustRepositoryImpl extends IllustRepository {
    def findIllustBy(id: IllustId)(implicit config: Config): Either[PixivException, Illust] = {
      findIllustDetailBy(id)(config)
    }

    def findIllustDetailBy(id: IllustId)(implicit config: Config): Either[PixivException, IllustDetail] = {
      val illustDetailUrl = {
        val iu = illustUrlBase + "illust_id=" + id.value
        config.authToken match {
          case Some(a) => iu + "&PHPSESSID=" + a
          case None => iu
        }
      }

      try {
        val http = new Http
        http.userAgent("Yapix")

        val response = http.get(illustDetailUrl)
        val responseString = response.asString

        val illustDetail = IllustDetailCsvParser.parse(responseString)

        Right(illustDetail)
      } catch {
        case e: IOException => throw new PixivException(IOError, e)
        case e: Exception => throw new PixivException(UnknownError, e)
      }
    }
  }
}

private[pixiv] class IllustImpl(@transient val illust: IllustComponent,
  val identity: IllustId,
  val title: String,
  val thumbnailImageUrl: String) extends Illust {

  private[this] var illustDetail: Option[IllustDetail] = None

  def detail(implicit config: Config) = {
    synchronized {
      illustDetail match {
        case Some(i) => Right(i)
        case None =>
          illust.illust.findIllustDetailBy(identity)(config) match {
            case Right(i) =>
              illustDetail = Some(i)
              Right(i)
            case Left(e) => Left(e)
          }
      }
    }
  }

  override def toString = identity.value
}

private[pixiv] class IllustDetailImpl(
  val illust: Illust,
  val caption: String,
  val postedDateTime: Date,
  val middleImageUrl: String,
  val imageUrl: String,
  val tags: List[String],
  val pageCount: Int,
  val viewCount: Int,
  val evaluationCount: Int,
  val evaluation: Int,
  val bookmarkCount: Int,
  val user: User) extends IllustDetail {

  def detail(implicit config: Config): Right[PixivException, IllustDetail] = Right(this)
}
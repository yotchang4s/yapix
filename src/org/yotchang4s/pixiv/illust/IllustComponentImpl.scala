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
        case e: IOException => throw new PixivException(IOError, Some(e))
        case e: Exception => throw new PixivException(UnknownError, Some(e))
      }
    }
  }
}

private[pixiv] class IllustImpl(@transient val illust: IllustComponent,
  val identity: IllustId,
  val title: String,
  val thumbnailImageUrl: String) extends Illust {

  @transient
  lazy val illustRepository: illust.IllustRepository = illust.illust

  def detail(implicit config: Config) = illustRepository.findIllustDetailBy(identity)(config)

  override def toString = identity.value
}

private[this] class IllustDetailImpl(
  val illust: Illust,
  val caption: String,
  val middleImageUrl: String,
  val imageUrl: String,
  val tags: List[String],
  val totalScore: Int,
  val viewCount: Int,
  val bookmarkCount: Int,
  val user: User) extends IllustDetail {

  def detail(implicit config: Config): Right[PixivException, IllustDetail] = Right(this)
}
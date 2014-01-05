package org.yotchang4s.pixiv.ranking

import org.yotchang4s.pixiv._
import org.yotchang4s.pixiv.http._
import org.yotchang4s.pixiv.illust._
import org.yotchang4s.pixiv.tag._
import org.yotchang4s.pixiv.illust._
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.ObjectOutputStream
import java.io.ObjectInputStream
import java.io.IOException
import org.yotchang4s.pixiv.PixivException._

private[pixiv] trait RankingComponentImpl extends RankingComponent { this: IllustComponent =>
  private val rankingUrlBase = "http://www.pixiv.net/ranking.php?format=json&"
  private val novelRankingUrlBase = "http://www.pixiv.net/ranking.php?"

  class RankingRepositoryImpl extends RankingRepository {
    def overall(rankingType: OverallRankingType, page: Int)(implicit config: Config): Either[PixivException, List[RankingIllust]] = {
      import Overall._

      val ovarallRankingUrl = rankingType match {
        case Daily => rankingUrlBase + "mode=daily"
        case Weekly => rankingUrlBase + "mode=weekly"
        case Monthly => rankingUrlBase + "mode=monthly"
        case Rookie => rankingUrlBase + "mode=rookie"
        case Original => rankingUrlBase + "mode=original"
        case Male => rankingUrlBase + "mode=male"
        case Female => rankingUrlBase + "mode=female"
        case DailyR18 => rankingUrlBase + "mode=daily_r18"
        case WeeklyR18 => rankingUrlBase + "mode=weekly_r18"
        case MaleR18 => rankingUrlBase + "mode=male_r18"
        case FemaleR18 => rankingUrlBase + "mode=female_r18"
      }

      getIllust(ovarallRankingUrl, page)(config)
    }

    def illust(rankingType: IllustRankingType, page: Int)(implicit config: Config): Either[PixivException, List[RankingIllust]] = {
      import Illust._

      val ovarallRankingUrl = rankingType match {
        case Daily => rankingUrlBase + "mode=daily"
        case Weekly => rankingUrlBase + "mode=weekly"
        case Monthly => rankingUrlBase + "mode=monthly"
        case Rookie => rankingUrlBase + "mode=rookie"
        case DailyR18 => rankingUrlBase + "mode=daily_r18"
        case WeeklyR18 => rankingUrlBase + "mode=weekly_r18"
        case MaleR18 => rankingUrlBase + "mode=male_r18"
        case FemaleR18 => rankingUrlBase + "mode=female_r18"
      }

      getIllust(ovarallRankingUrl + "&content=illust", page)(config)
    }

    private def getIllust(baseUrl: String, page: Int)(implicit config: Config): Either[PixivException, List[RankingIllust]] = {
      import Overall._

      val http = new Http
      http.userAgent("Yapix")

      var response: HttpResponse = null
      try {
        val cookie = config.authToken.map(HttpCookie("PHPSESSID", _))

        response = http.get(baseUrl + "&p=" + page, None, None, cookie.map(List(_)))
        val body = response.asString("UTF-8")

        val rankings = toRankingIllust(body)

        return Right(rankings)

      } catch {
        case e: PixivException => Left(e)
        case e: IOException => Left(new HttpResponseException(response, Some(e)))
      }
    }

    def manga(rankingType: MangaRankingType, page: Int)(implicit config: Config): Either[PixivException, List[RankingIllust]] =
      Left(new PixivException(NoImplements))
    def novel(rankingType: NovelRankingType, page: Int)(implicit config: Config): Either[PixivException, List[RankingIllust]] =
      Left(new PixivException(NoImplements))
  }

  private def toRankingIllust(body: String) = {
    val content = (new Gson).fromJson(body, classOf[Content])
    if (content.contents == null) {
      val error = (new Gson).fromJson(body, classOf[Error])
      if (error.error == null) {
        throw new PixivException(IOError, Some("Unknown IO Error"))
      }
      throw new PixivException(IOError, Some(error.error))
    }

    import scala.collection.convert.WrapAsScala._
    content.contents.map { rankingJson =>
      new RankingIllustImpl(
        this,
        IllustId(rankingJson.illust_id),
        rankingJson.title,
        rankingJson.width,
        rankingJson.height,
        rankingJson.tags.map(t => new Tag(TagId(t))).toList,
        rankingJson.url,
        rankingJson.yes_rank,
        rankingJson.total_score,
        rankingJson.view_count)
    }.toList
  }
}

private case class Content(val contents: java.util.ArrayList[RankingJson])
private case class Error(val error: String)

private[this] case class RankingJson(
  illust_id: String,
  title: String,
  width: Int,
  height: Int,
  tags: java.util.List[String],
  url: String,
  user_name: String,
  profile_img: String,
  rank: Int,
  yes_rank: Int,
  total_score: Int,
  view_count: Int)

private class RankingIllustImpl(
  @transient val illust: IllustComponent,
  val identity: IllustId,
  val title: String,
  val width: Int,
  val height: Int,
  val tags: List[Tag],
  val url: String,
  val yesterdayRank: Int,
  val totalScore: Int,
  val viewCount: Int) extends RankingIllust {

  @transient
  lazy val illustRepository: illust.IllustRepository = illust.illust

  def detail(implicit config: Config) = illustRepository.findIllustDetailBy(identity)(config)

  override def toString = identity.value
}
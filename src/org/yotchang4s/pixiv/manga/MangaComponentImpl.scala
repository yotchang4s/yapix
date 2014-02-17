package org.yotchang4s.pixiv.manga

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
import org.yotchang4s.pixiv.illust.IllustId
import org.yotchang4s.pixiv.illust.IllustDetail
import org.yotchang4s.pixiv.illust.IllustDetailCsvParser
import org.yotchang4s.scala.Loan
import org.yotchang4s.scala.Loan._
import org.yotchang4s.pixiv.illust.IllustDetailImpl
import org.yotchang4s.pixiv.illust.Illust

private[pixiv] trait MangaComponentImpl extends MangaComponent {
  private val mangaUrlBase = "http://spapi.pixiv.net/iphone/manga.php?"

  class MangaRepositoryImpl extends MangaRepository {

    def findMangaBy(id: IllustId)(implicit config: Config): Either[PixivException, List[IllustDetail]] = {
      val mangaUrl = {
        val iu = mangaUrlBase + "illust_id=" + id.value
        config.authToken match {
          case Some(a) => iu + "&PHPSESSID=" + a
          case None => iu
        }
      }

      try {
        val http = new Http
        http.userAgent("Yapix")

        val response = http.get(mangaUrl)
        for (reader <- Loan(new BufferedReader(response.asReader("UTF-8")))) {
          Right(Iterator.continually(reader.readLine).takeWhile(null !=).map(IllustDetailCsvParser.parse(_)).toList)
        }
      } catch {
        case e: IOException => Left(new PixivException(IOError, e))
        case e: Exception => Left(new PixivException(UnknownError, e))
      }
    }
  }
}

package org.yotchang4s.pixiv.illust

import au.com.bytecode.opencsv.CSVParser
import org.yotchang4s.pixiv.user.User
import org.yotchang4s.pixiv.user.UserId
import org.yotchang4s.pixiv.user.UserId
import java.text.SimpleDateFormat
import org.yotchang4s.pixiv.PixivException
import org.yotchang4s.pixiv.PixivException.UnknownError
import java.io.Reader
import org.yotchang4s.scala.Loan
import org.yotchang4s.scala.Loan._
import java.io.BufferedReader

private[pixiv] object IllustDetailCsvParser {
  def parse(line: String): IllustDetail = {
    val parser = new CSVParser
    val columns = parser.parseLine(line)

    val illust = new IllustImpl(
      null,
      IllustId(columns(0)), //ID
      columns(3), // タイトル
      columns(9).replace("480mw", "240mw")) // サムネイル画像

    val format = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss")
    val postedDate = format.parse(columns(12))

    val pageCount = {
      val pageCountString = columns(19)
      if (pageCountString.isEmpty) {
        0
      } else {
        try {
          columns(19).toInt
        } catch {
          case e: NumberFormatException => throw new PixivException(UnknownError)
        }
      }
    }

    new IllustDetailImpl(
      illust,
      columns(18), // キャプション
      postedDate, // 投稿日時
      columns(9), // 中ぐらいの画像
      columns(9).replace("/mobile", "").replace("_480mw", ""), // 元画像
      columns(13).split(" ").toList, // タグ一覧
      pageCount,
      columns(17).toInt, // 閲覧数
      columns(15).toInt, // 評価回数
      columns(16).toInt, // 評価
      columns(22).toInt, // ブックマーク数
      new User(UserId(columns(1)), columns(5), columns(28))) // ユーザ情報(ID, 名前、プロフィール画像)
  }

  def parseIllusts(reader: Reader): List[IllustDetail] = {
    for (r <- Loan(new BufferedReader(reader))) {
      Stream.continually(r.readLine).takeWhile(null !=).map { l =>
        IllustDetailCsvParser.parse(l)
      }.toList
    }
  }
}

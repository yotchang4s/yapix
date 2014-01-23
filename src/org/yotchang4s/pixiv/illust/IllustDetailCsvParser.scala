package org.yotchang4s.pixiv.illust

import au.com.bytecode.opencsv.CSVParser
import org.yotchang4s.pixiv.user.User
import org.yotchang4s.pixiv.user.UserId
import org.yotchang4s.pixiv.user.UserId
import java.text.SimpleDateFormat

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

    new IllustDetailImpl(
      illust,
      columns(18), // キャプション
      postedDate, // 投稿日時
      columns(9), // 中ぐらいの画像
      columns(9).replace("/mobile", "").replace("_480mw", ""), // 元画像
      columns(13).split(" ").toList, // タグ一覧
      columns(17).toInt, // 閲覧数
      columns(15).toInt, // 評価回数
      columns(16).toInt, // 評価
      columns(22).toInt, // ブックマーク数
      new User(UserId(columns(1)), columns(5), columns(28))) // ユーザ情報(ID, 名前、プロフィール画像)
  }
}

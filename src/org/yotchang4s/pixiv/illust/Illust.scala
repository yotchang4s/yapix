package org.yotchang4s.pixiv.illust

import org.yotchang4s.pixiv.tag._
import org.yotchang4s.pixiv._
import org.yotchang4s.pixiv.user.User

case class IllustId(value: String) extends Identity[String]

trait Illust extends Entity[IllustId] {
  val identity: IllustId
  val title: String
  val thumbnailImageUrl: String

  def detail(implicit config: Config): Either[PixivException, IllustDetail]
}

trait IllustDetail extends Illust {
  val illust: Illust
  val identity: IllustId = illust.identity
  val title: String = illust.title
  val caption: String
  val thumbnailImageUrl: String = illust.thumbnailImageUrl
  val middleImageUrl: String
  val imageUrl: String
  val tags: List[String]

  val totalScore: Int
  val viewCount: Int
  val bookmarkCount: Int

  val user: User
}
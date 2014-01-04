package org.yotchang4s.pixiv.illust

import org.yotchang4s.pixiv.tag._
import org.yotchang4s.pixiv._

case class IllustId(value: String) extends Identity[String]

trait Illust extends Entity[IllustId] {
  val identity: IllustId
  val title: String
  val width: Int
  val height: Int
  val tags: List[Tag]
  val url: String

  def detail(implicit config: Config): Either[PixivException, IllustDetail]
}

trait IllustDetail extends Illust {

  val illust: Illust

  final val identity = illust.identity
  final val title: String = illust.title
  final val width: Int = illust.width
  final val height: Int = illust.height
  final val tags: List[Tag] = illust.tags
  final val url: String = illust.url

}
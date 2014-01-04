package org.yotchang4s.pixiv.illust

import org.yotchang4s.pixiv._
import org.yotchang4s.pixiv.illust._

trait IllustComponent {
  @transient
  val illust: IllustRepository

  trait IllustRepository {
    def findIllustBy(identity: IllustId)(implicit config: Config): Either[PixivException, Illust]
    def findIllustDetailBy(identity: IllustId)(implicit config: Config): Either[PixivException, IllustDetail]
  }
}
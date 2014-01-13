package org.yotchang4s.pixiv.bookmark

import org.yotchang4s.pixiv.Config
import org.yotchang4s.pixiv.illust.Illust
import org.yotchang4s.pixiv.PixivException

object BookmarkComponent {
  sealed trait PrivacyType
  case object Open extends PrivacyType
  case object Close extends PrivacyType
}

trait BookmarkComponent {
  import BookmarkComponent._

  val bookmark: BookmarkRepository

  trait BookmarkRepository {
    def findSelfBookmarks(privacyType: PrivacyType, page: Int)(implicit config: Config): Either[PixivException, List[Illust]]
  }
}
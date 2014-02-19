package org.yotchang4s.pixiv.search

import org.yotchang4s.pixiv._
import org.yotchang4s.pixiv.illust._
import org.yotchang4s.pixiv.tag.Tag

trait SearchComponent {
  @transient
  val search: SearchRepository

  trait SearchRepository {
    def search(tag: Tag, page: Int)(implicit config: Config): Either[PixivException, List[IllustDetail]]

    def search(caption: Caption, page: Int)(implicit config: Config): Either[PixivException, List[IllustDetail]]
  }
}
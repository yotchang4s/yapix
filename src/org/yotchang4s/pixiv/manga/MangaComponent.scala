package org.yotchang4s.pixiv.manga

import org.yotchang4s.pixiv._
import org.yotchang4s.pixiv.illust._

trait MangaComponent {
  val manga: MangaRepository

  trait MangaRepository {
    def findMangaBy(identity: IllustId)(implicit config: Config): Either[PixivException, List[IllustDetail]]
  }
}
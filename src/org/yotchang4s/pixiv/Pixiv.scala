package org.yotchang4s.pixiv

import org.yotchang4s.pixiv.ranking._
import org.yotchang4s.pixiv.illust._
import org.yotchang4s.pixiv.novel._
import org.yotchang4s.pixiv.tag._
import org.yotchang4s.pixiv.user._

case object Pixiv extends RankingComponentImpl
  with IllustComponentImpl with NovelComponentImpl with TagComponentImpl with UserComponentImpl {

  val ranking = new RankingRepositoryImpl
  val illust = new IllustRepositoryImpl
  val novel = new NovelRepositoryImpl
  val tag = new TagRepositoryImpl
  val user = new UserRepositoryImpl
}
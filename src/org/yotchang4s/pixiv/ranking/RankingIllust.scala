package org.yotchang4s.pixiv.ranking

import org.yotchang4s.pixiv.illust._
import org.yotchang4s.pixiv.tag._

trait RankingIllust extends Illust {
  val yesterdayRank: Int
  val totalScore: Int
  val viewCount: Int
}
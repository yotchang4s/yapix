package org.yotchang4s.pixiv.ranking

import org.yotchang4s.pixiv.illust._
import org.yotchang4s.pixiv.Config
import org.yotchang4s.pixiv.PixivException

trait RankingComponent {
  val ranking: RankingRepository

  trait RankingRepository {
    def overall(rankingType: OverallRankingType, page: Int)(implicit config: Config): Either[PixivException, List[RankingIllust]]
    def illust(rankingType: IllustRankingType, page: Int)(implicit config: Config): Either[PixivException, List[RankingIllust]]
    def manga(rankingType: MangaRankingType, page: Int)(implicit config: Config): Either[PixivException, List[RankingIllust]]
    def novel(rankingType: NovelRankingType, page: Int)(implicit config: Config): Either[PixivException, List[RankingIllust]]
  }
}

@serializable
sealed trait RankingCategory extends Serializable

sealed trait RankingType[T]

sealed trait OverallRankingType extends RankingType[OverallRankingType]
case object Overall extends RankingCategory {
  case object Daily extends OverallRankingType
  case object Weekly extends OverallRankingType
  case object Monthly extends OverallRankingType
  case object Rookie extends OverallRankingType
  case object Original extends OverallRankingType
  case object Male extends OverallRankingType
  case object Female extends OverallRankingType

  case object DailyR18 extends OverallRankingType
  case object WeeklyR18 extends OverallRankingType
  case object MaleR18 extends OverallRankingType
  case object FemaleR18 extends OverallRankingType
}

sealed trait IllustRankingType extends RankingType[IllustRankingType]
case object Illust extends RankingCategory {
  case object Daily extends IllustRankingType
  case object Weekly extends IllustRankingType
  case object Monthly extends IllustRankingType
  case object Rookie extends IllustRankingType

  case object DailyR18 extends IllustRankingType
  case object WeeklyR18 extends IllustRankingType
  case object MaleR18 extends IllustRankingType
  case object FemaleR18 extends IllustRankingType
}

sealed trait MangaRankingType extends RankingType[MangaRankingType]
case object Manga extends RankingCategory {
  case object Daily extends MangaRankingType
  case object Weekly extends MangaRankingType
  case object Monthly extends MangaRankingType
  case object Rookie extends MangaRankingType

  case object DailyR18 extends MangaRankingType
  case object WeeklyR18 extends MangaRankingType
  case object MaleR18 extends MangaRankingType
  case object FemaleR18 extends MangaRankingType
}

sealed trait NovelRankingType extends RankingType[NovelRankingType]
case object Novel extends RankingCategory {
  case object Daily extends NovelRankingType
  case object Weekly extends NovelRankingType
  case object Rookie extends NovelRankingType

  case object DailyR18 extends NovelRankingType
  case object WeeklyR18 extends NovelRankingType
}
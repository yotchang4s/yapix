package org.yotchang4s.yapix

import scala.concurrent._

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view._
import android.widget.AbsListView.OnScrollListener
import android.widget.AdapterView.OnItemSelectedListener
import android.widget._
import org.yotchang4s.android._
import org.yotchang4s.android.Listeners._
import org.yotchang4s.pixiv._
import org.yotchang4s.pixiv.illust._
import org.yotchang4s.pixiv.ranking._
import org.yotchang4s.yapix.YapixConfig._

class RankingFragment extends QuickReturnGridViewFragment {
  protected val layout: Int = R.layout.ranking_fragment
  protected val observableGridViewId: Int = R.id.rankingGridview

  private[this] val TAG = getClass.getSimpleName

  private[this]type RankingGridAdapter = ListGridViewImageAdapter[RankingIllust]

  private[this] var quickReturnView: Spinner = null
  private[this] var rankingGridAdapter: RankingGridAdapter = null
  private[this] var rankingTypeAdapter: RankingTypeAdapter = null
  private[this] var rankingCategory: RankingCategory = null

  private[this] var currentFuture: Option[(Future[Either[PixivException, List[RankingIllust]]], () => Boolean)] = None

  private[this] var nowRankingType: RankingType[_] = Overall.Daily

  private[this] var scrollLast = false
  private[this] var rankings: List[RankingIllust] = Nil
  private[this] var rankingsPage = 0

  setRetainInstance(true)

  protected override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)

    rankingCategory =
      Option(getArguments.get(ArgumentKeys.RankingCategory).asInstanceOf[RankingCategory]) match {
        case Some(x) => x
        case None => Overall
      }
  }

  override protected def onCreateView(inflater: LayoutInflater, container: ViewGroup,
    savedInstanceState: Bundle): View = {
    val view = super.onCreateView(inflater, container, savedInstanceState);

    gridView.onScrolls += { (view, firstVisibleItem, visibleItemCount, totalItemCount) =>
      scrollLast = totalItemCount != 0 && totalItemCount == firstVisibleItem + visibleItemCount
    }
    gridView.onScrollChanges += { (view, scrollState) =>
      if (RankingFragment.this.scrollLast && scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
        paging(nowRankingType)
        scrollLast = false
      }
    }

    gridView.onItemClicks += { (parent, view, position, id) =>
      val tran = getFragmentManager.beginTransaction

      val fragment = new IllustViewPagerFragment

      val bundle = new Bundle
      bundle.putSerializable(ArgumentKeys.IllustList, rankings.toArray)
      bundle.putInt(ArgumentKeys.IllustListPosition, position)

      fragment.setArguments(bundle)

      tran.add(R.id.content, fragment, "YHAAAAAA")
      tran.hide(this)
      tran.addToBackStack(null)
      tran.commit
    }
    view
  }

  protected def createQuickReturnView(rootView: View): View = {
    quickReturnView = rootView.findViewById(R.id.rankingQuickReturnSticky).asInstanceOf[Spinner]

    quickReturnView.onItemSelecteds += { (parent, view, position, id) =>
      val newRankingLabels =
        for ((label, i) <- rankingTypeAdapter.getRankingLabels.zipWithIndex) yield {
          i match {
            case p if (p == position) =>
              RankingLabel(label.text, label.existR18, label.r18)
            case _ => label
          }
        }
      rankingTypeAdapter.setRankingLabels(rankingTypeAdapter.getRankingLabels)

      val oldRankingType = nowRankingType
      nowRankingType = getCurrentRankingType(rankingCategory, position, rankingTypeAdapter.getRankingLabels(position).r18)
      if (nowRankingType != oldRankingType) {
        rankings = Nil
      }
      paging(nowRankingType)
    }

    quickReturnView
  }

  protected override def onActivityCreated(savedInstanceState: Bundle) {
    super.onActivityCreated(savedInstanceState)

    rankingGridAdapter = new RankingGridAdapter(getActivity.getApplicationContext, 100, 5)
    rankingGridAdapter.setList(rankings)
    gridView.setAdapter(rankingGridAdapter)

    rankingTypeAdapter = new RankingTypeAdapter(this.quickReturnView)
    rankingTypeAdapter.setRankingLabels(createRankingLabels(rankingCategory))
    quickReturnView.setAdapter(rankingTypeAdapter)
  }

  private def createRankingLabels(rankingCategory: RankingCategory): List[RankingLabel] = {
    val getStrings = { resId: Int => getActivity.getResources.getStringArray(resId).toList }

    rankingCategory match {
      case Overall => createOverallRankingLabels(getStrings(R.array.ranking_type_overall))
      case Illust => createIllustRankingLabels(getStrings(R.array.ranking_type_illust))
      case Manga => createMangaRankingLabels(getStrings(R.array.ranking_type_manga))
      case Novel => createNovelRankingLabels(getStrings(R.array.ranking_type_novel))
    }
  }

  private def createOverallRankingLabels(status: List[String]): List[RankingLabel] = {
    for ((text, i) <- status.zipWithIndex) yield {
      val existR18 = i match {
        case 0 => true // デイリー
        case 1 => true // ウィークリー
        case 5 => true // 男子に人気
        case 6 => true // 女子に人気
        case x => false
      }
      RankingLabel(text, existR18, false)
    }
  }

  private def createIllustRankingLabels(status: List[String]): List[RankingLabel] = {
    for ((text, i) <- status.zipWithIndex) yield {
      val existR18 = i match {
        case 0 => true // デイリー
        case 1 => true // ウィークリー
        case x => false
      }
      RankingLabel(text, existR18, false)
    }
  }

  private def createMangaRankingLabels(status: List[String]): List[RankingLabel] = {
    for ((text, i) <- status.zipWithIndex) yield {
      val existR18 = i match {
        case 0 => true // デイリー
        case 1 => true // ウィークリー
        case x => false
      }
      RankingLabel(text, existR18, false)
    }
  }

  private def createNovelRankingLabels(status: List[String]): List[RankingLabel] = {
    for ((text, i) <- status.zipWithIndex) yield {
      val existR18 = i match {
        case 0 => true // デイリー
        case 1 => true // ウィークリー
        case x => false
      }
      RankingLabel(text, existR18, false)
    }
  }

  private def getCurrentRankingType(rankingCategory: RankingCategory, position: Int, r18: Boolean) = {

    def getOverallRankingType(position: Int, r18: Boolean) = position match {
      case 0 => if (!r18) Overall.Daily else Overall.DailyR18
      case 1 => if (!r18) Overall.Weekly else Overall.WeeklyR18
      case 2 => Overall.Monthly
      case 3 => Overall.Rookie
      case 4 => Overall.Original
      case 5 => if (!r18) Overall.Male else Overall.MaleR18
      case 6 => if (!r18) Overall.Female else Overall.FemaleR18
    }

    def getIllustRankingType(position: Int, r18: Boolean) = position match {
      case 0 => if (!r18) Illust.Daily else Illust.DailyR18
      case 1 => if (!r18) Illust.Weekly else Illust.WeeklyR18
      case 2 => Illust.Monthly
      case 3 => Illust.Rookie
      case 4 => Illust.MaleR18
      case 5 => Illust.FemaleR18
    }

    def getMangaRankingType(position: Int, r18: Boolean) =
      position match {
        case 0 => if (!r18) Manga.Daily else Manga.DailyR18
        case 1 => if (!r18) Manga.Weekly else Manga.WeeklyR18
        case 2 => Manga.Monthly
        case 3 => Manga.Rookie
        case 4 => Manga.MaleR18
        case 5 => Manga.FemaleR18
      }

    def getNovelRankingType(position: Int, r18: Boolean) =
      position match {
        case 0 => if (!r18) Novel.Daily else Novel.DailyR18
        case 1 => if (!r18) Novel.Weekly else Novel.WeeklyR18
        case 2 => Novel.Rookie
      }

    rankingCategory match {
      case Overall => getOverallRankingType(position, r18)
      case Illust => getIllustRankingType(position, r18)
      case Manga => getMangaRankingType(position, r18)
      case Novel => getNovelRankingType(position, r18)
    }
  }

  private def get(rankingType: RankingType[_], page: Int): Either[PixivException, List[RankingIllust]] = {
    rankingType match {
      case t: OverallRankingType => Pixiv.ranking.overall(t, page)
      case t: IllustRankingType => Pixiv.ranking.illust(t, page)
      case t: MangaRankingType => Pixiv.ranking.manga(t, page)
      case t: NovelRankingType => Pixiv.ranking.novel(t, page)
    }
  }

  private def cancellableFuture[T](fun: Future[T] => T)(implicit ex: ExecutionContext): (Future[T], () => Boolean) = {
    val p = Promise[T]()
    val f = p.future
    p tryCompleteWith Future(fun(f))
    (f, () => p.tryFailure(new CancellationException))
  }

  private def paging(rankingType: RankingType[_]) {
    import scala.concurrent._
    import ExecutionContext.Implicits.global

    if (rankings == Nil) {
      rankingsPage = 1
      rankingGridAdapter.setList(rankings)
      gridView.invalidateComputeScrollY
      rankingGridAdapter.notifyDataSetChanged
    }

    currentFuture match {
      case Some(f) =>
        f._2()
      case None =>
    }

    val (future, cancel) = cancellableFuture[Either[PixivException, List[RankingIllust]]](future => {
      get(rankingType, rankingsPage)
    })

    currentFuture = Some((future, cancel))

    future.onSuccess {
      case Right(x) =>
        rankings = rankings ::: x
        rankingGridAdapter.setList(rankings)
        gridView.invalidateComputeScrollY
        rankingGridAdapter.notifyDataSetChanged

        rankingsPage = rankingsPage + 1

      case Left(e) =>
        if (rankingsPage == 1) {
          val message = if (e.getMessage != null) "\n" + e.getMessage else ""

          ToastMaster.makeText(getActivity, "接続に失敗しました" + message, Toast.LENGTH_LONG).show
        }
        Log.w(TAG, "接続失敗", e);

    }(new UIExecutionContext())

    future.onFailure {
      case e: CancellationException =>
        currentFuture = None
    }(new UIExecutionContext())
  }
}
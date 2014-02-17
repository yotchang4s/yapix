package org.yotchang4s.yapix.search

import scala.concurrent._
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view._
import android.widget.AbsListView._
import android.widget._

import org.yotchang4s.android._
import org.yotchang4s.android.Listeners._
import org.yotchang4s.pixiv._
import org.yotchang4s.pixiv.illust._
import org.yotchang4s.pixiv.ranking._
import org.yotchang4s.yapix.YapixConfig._
import org.yotchang4s.yapix._

import org.yotchang4s.pixiv.tag._

class SearchResultFragment extends AbstractFragment {
  import SearchFragment._

  private val TAG = getClass.getSimpleName

  private[this] var searchType: SearchType = null
  private[this] var searchKeyword: String = ""

  private[this] var searchGridAdapter: ListGridViewImageAdapter[IllustDetail] = null

  private[this] var currentFuture: Option[(Future[Either[PixivException, List[Illust]]], () => Boolean)] = None

  private[this] var scrollLast = false
  private[this] var searchPage = 0
  private[this] var gridViewPosition = 0
  private[this] var gridViewTop = 0

  private[this] var gridView: GridViewV16 = null

  private[this] var illustViewPagerFragment: IllustViewPagerFragment = null

  setRetainInstance(true)

  protected override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)

    searchType = getArguments.get(ArgumentKeys.SearchType).asInstanceOf[SearchType]
    searchKeyword = getArguments.getString(ArgumentKeys.SearchKeyword)
  }

  protected override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val view = inflater.inflate(R.layout.search_result, container, false)

    gridView = view.findViewById(R.id.searchGridview).asInstanceOf[GridViewV16]

    gridView.onScrolls += { (view, firstVisibleItem, visibleItemCount, totalItemCount) =>
      scrollLast = totalItemCount != 0 && totalItemCount == firstVisibleItem + visibleItemCount
    }
    gridView.onScrollStateChangeds += { (view, scrollState) =>
      if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
        gridViewPosition = gridView.getFirstVisiblePosition
        gridViewTop = if (gridView.getChildCount > 0) gridView.getChildAt(0).getTop else 0
        if (scrollLast) {
          paging
          scrollLast = false
        }
      }
    }

    gridView.onItemClicks += { (parent, view, position, id) =>
      val tran = getChildFragmentManager.beginTransaction

      illustViewPagerFragment = new IllustViewPagerFragment
      childFragment(illustViewPagerFragment)

      val bundle = new Bundle
      bundle.putSerializable(ArgumentKeys.IllustList, searchGridAdapter.getList.toArray)
      bundle.putInt(ArgumentKeys.IllustListPosition, position)

      illustViewPagerFragment.setArguments(bundle)

      tran.add(R.id.searchResultContent, illustViewPagerFragment)
      tran.addToBackStack(null)
      tran.commit
    }
    view
  }

  protected override def onDestroyView {
    super.onDestroyView

    gridView.onScrolls.clear
    gridView.onScrollStateChangeds.clear
    gridView.onItemClicks.clear
  }

  protected override def onActivityCreated(savedInstanceState: Bundle) {
    super.onActivityCreated(savedInstanceState)

    if (searchGridAdapter == null) {
      searchGridAdapter = new ListGridViewImageAdapter(getActivity.getApplicationContext, 1)
    }
    gridView.setAdapter(searchGridAdapter)

    gridView.setSelection(gridViewPosition)
    gridView.smoothScrollBy(-gridViewTop, 0)

    paging
  }

  private def paging {

    if (searchGridAdapter.getList == Nil) {
      this.searchPage = 1
      searchGridAdapter.setList(Nil)
      searchGridAdapter.notifyDataSetChanged
    }

    currentFuture match {
      case Some(f) => f._2()
      case None =>
    }

    val searchType = this.searchType
    val searchKeyword = this.searchKeyword
    val searchPage = this.searchPage

    import scala.concurrent._
    import ExecutionContext.Implicits.global

    val f = future {
      searchType match {
        case Tag =>
          Pixiv.search.searchTag(new Tag(TagId(searchKeyword)), searchPage)
      }
    }

    f.onSuccess {
      case Right(x) =>
        val searchResult = searchGridAdapter.getList ::: x
        searchGridAdapter.setList(searchResult)
        searchGridAdapter.notifyDataSetChanged

        this.searchPage = searchPage + 1

      case Left(e) =>
        error(TAG, getActivity.getString(R.string.accessFailure), e);

    }(new UIExecutionContext())
  }
}
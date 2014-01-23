package org.yotchang4s.yapix

import scala.concurrent._
import android.os.Bundle
import android.util.Log
import android.view._
import android.widget.AbsListView.OnScrollListener
import android.widget._
import org.yotchang4s.android._
import org.yotchang4s.android.Listeners._
import org.yotchang4s.pixiv._
import org.yotchang4s.yapix.YapixConfig._
import org.yotchang4s.pixiv.bookmark._
import org.yotchang4s.pixiv.bookmark.BookmarkComponent._
import org.yotchang4s.pixiv.illust.Illust
import android.widget.CompoundButton.OnCheckedChangeListener

class BookmarkFragment extends QuickReturnGridViewFragment {
  protected val layout: Int = R.layout.bookmark_fragment
  protected val observableGridViewId: Int = R.id.bookmarkGridview

  private[this] val TAG = getClass.getSimpleName

  private[this] var bookmarkGridAdapter: BookmarkGridAdapter = null

  private[this] var currentFuture: Option[(Future[Either[PixivException, List[Illust]]], () => Boolean)] = None

  private[this] var nowPrivacyType: PrivacyType = Open

  private[this] var scrollLast = false
  private[this] var bookmarks: List[Illust] = Nil
  private[this] var rankingsPage = 0

  private[this] val gridViewPositionKey = "gridViewPositionKey"

  setRetainInstance(true)

  override protected def onCreateView(inflater: LayoutInflater, container: ViewGroup,
    savedInstanceState: Bundle): View = {
    val view = super.onCreateView(inflater, container, savedInstanceState);

    gridView.onScrolls += { (view, firstVisibleItem, visibleItemCount, totalItemCount) =>
      scrollLast = totalItemCount != 0 && totalItemCount == firstVisibleItem + visibleItemCount
    }
    gridView.onScrollStateChangeds += { (view, scrollState) =>
      if (BookmarkFragment.this.scrollLast && scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
        paging(nowPrivacyType)
        scrollLast = false
      }
    }

    gridView.onItemClicks += { (parent, view, position, id) =>
      val tran = getChildFragmentManager.beginTransaction

      val fragment = new IllustViewPagerFragment

      val bundle = new Bundle
      bundle.putSerializable(ArgumentKeys.IllustList, bookmarks.toArray)
      bundle.putInt(ArgumentKeys.IllustListPosition, position)

      fragment.setArguments(bundle)

      tran.add(R.id.bookmarkContent, fragment)
      tran.addToBackStack(null)
      tran.commit
    }
    view
  }

  protected def createQuickReturnView(rootView: View): View = {
    val quickReturnView = rootView.findViewById(R.id.bookmarkQuickReturnSticky).asInstanceOf[ViewGroup]

    val privacySwitch = quickReturnView.findViewById(R.id.bookmarkPrivacyTypeSwitch).asInstanceOf[Switch]

    privacySwitch.setOnCheckedChangeListener(new OnCheckedChangeListener {
      def onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        isChecked match {
          case true =>
            nowPrivacyType = Close
            bookmarks = Nil
            paging(Close)
          case false =>
            nowPrivacyType = Open
            bookmarks = Nil
            paging(Open)
        }
      }
    })

    quickReturnView
  }

  protected override def onActivityCreated(savedInstanceState: Bundle) {
    super.onActivityCreated(savedInstanceState)

    bookmarkGridAdapter = new BookmarkGridAdapter(getActivity.getApplicationContext, 1)
    bookmarkGridAdapter.setList(bookmarks)
    gridView.setAdapter(bookmarkGridAdapter)

    paging(nowPrivacyType)
  }

  override def onBackPressed = {
    if (getChildFragmentManager.getBackStackEntryCount() > 0) {
      getChildFragmentManager.popBackStack
      true
    } else {
      false
    }
  }

  private def get(privacyType: PrivacyType, page: Int): Either[PixivException, List[Illust]] = {
    privacyType match {
      case Open => Pixiv.bookmark.findSelfBookmarks(Open, page)
      case Close => Pixiv.bookmark.findSelfBookmarks(Close, page)
    }
  }

  private def paging(privacyType: PrivacyType) {
    import scala.concurrent._
    import ExecutionContext.Implicits.global

    if (bookmarks == Nil) {
      rankingsPage = 1
      bookmarkGridAdapter.setList(bookmarks)
      gridView.invalidateComputeScrollY
      bookmarkGridAdapter.notifyDataSetChanged
    }

    currentFuture match {
      case Some(f) => f._2()
      case None =>
    }

    import org.yotchang4s.scala.FutureUtil._
    val (future, cancel) = cancellableFuture[Either[PixivException, List[Illust]]](future => {
      get(privacyType, rankingsPage)
    })

    currentFuture = Some((future, cancel))

    future.onSuccess {
      case Right(x) =>
        bookmarks = bookmarks ::: x
        bookmarkGridAdapter.setList(bookmarks)
        gridView.invalidateComputeScrollY
        bookmarkGridAdapter.notifyDataSetChanged

        rankingsPage = rankingsPage + 1

      case Left(e) =>
        if (rankingsPage == 1) {
          val message = if (e.getMessage != null) "\n" + e.getMessage else ""

          ToastMaster.makeText(getActivity, "接続に失敗しました" + message, Toast.LENGTH_LONG).show
        }
        Log.w(TAG, "接続失敗", e);

    }(new UIExecutionContext())

    future.onFailure {
      case e: CancellationException => currentFuture = None
    }(new UIExecutionContext())
  }
}
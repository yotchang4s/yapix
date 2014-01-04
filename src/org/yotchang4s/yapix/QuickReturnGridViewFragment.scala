package org.yotchang4s.yapix

import scala.util.Failure
import android.content.Context
import android.graphics.Canvas
import android.util.Log
import android.os._
import android.view._
import android.view.animation._
import android.widget._
import org.yotchang4s.android._
import org.yotchang4s.android.Listeners._
import org.yotchang4s.pixiv._
import org.yotchang4s.pixiv.ranking._
import org.yotchang4s.yapix.YapixConfig._
import android.support.v4.app.Fragment
import android.annotation.TargetApi

trait QuickReturnGridViewFragment extends Fragment {
  private sealed trait State
  private case object STATE_ONSCREEN extends State
  private case object STATE_OFFSCREEN extends State
  private case object STATE_RETURNING extends State

  private[this] var quickReturnView: View = null
  private[this] var observableGrideView: ObservableGridView = null
  private[this] var minRawY: Int = 0
  private[this] var state: State = STATE_ONSCREEN
  private[this] var quickReturnHeight: Int = 0

  private[this] var cachedVerticalScrollRange = 0

  protected val layout: Int
  protected val observableGridViewId: Int

  def gridView: ObservableGridView = observableGrideView

  protected def createQuickReturnView(rootView: View): View

  override protected def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val rootView = inflater.inflate(layout, container, false).asInstanceOf[ViewGroup]

    observableGrideView = rootView.findViewById(observableGridViewId).asInstanceOf[ObservableGridView]

    quickReturnView = createQuickReturnView(rootView)

    observableGrideView.getViewTreeObserver.addOnGlobalLayoutListener(
      new ViewTreeObserver.OnGlobalLayoutListener {
        def onGlobalLayout {
          quickReturnHeight = quickReturnView.getHeight
          observableGrideView.computeScrollY
          cachedVerticalScrollRange = observableGrideView.getGridHeight
          // 要素0対策
          if (observableGrideView.getAdapter.getCount() == 0) {
            onScroll(observableGrideView, -1, 0, 0)
          }
        }
      })

    observableGrideView.onScrolls += (onScroll _)

    return rootView
  }

  private def onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
    val mScrollY = observableGrideView.getComputedScrollY

    val rawY = -Math.min(cachedVerticalScrollRange, mScrollY)

    val translationY = state match {
      case STATE_OFFSCREEN =>
        if (rawY <= minRawY) {
          minRawY = rawY
        } else {
          state = STATE_RETURNING
        }
        rawY

      case STATE_ONSCREEN =>
        if (rawY < -quickReturnHeight) {
          state = STATE_OFFSCREEN
          minRawY = rawY
        }
        rawY

      case STATE_RETURNING =>
        var ty = (rawY - minRawY) - quickReturnHeight
        if (ty > 0) {
          ty = 0
          minRawY = rawY - quickReturnHeight
        }
        if (rawY > 0) {
          state = STATE_ONSCREEN
          ty = rawY
        }
        if (ty < -quickReturnHeight) {
          state = STATE_OFFSCREEN
          minRawY = rawY
        }
        ty
    }

    val anim = new TranslateAnimation(0, 0, translationY, translationY)
    anim.setFillAfter(true)
    anim.setDuration(0)
    quickReturnView.startAnimation(anim)
  }
}
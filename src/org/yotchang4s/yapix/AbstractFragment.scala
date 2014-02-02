package org.yotchang4s.yapix

import android.support.v4.app.Fragment
import org.yotchang4s.pixiv.PixivException
import org.yotchang4s.pixiv.PixivException._
import android.util.Log
import org.yotchang4s.android.ToastMaster
import android.widget.Toast

abstract class AbstractFragment extends Fragment {

  private var _childFragment: Option[AbstractFragment] = None

  protected def childFragment(childFragment: AbstractFragment) = _childFragment = Option(childFragment)
  protected def childFragment: Option[AbstractFragment] = _childFragment

  protected[yapix] def onBackPressed: Boolean = {
    val noStack = {
      var s = childFragment match {
        case Some(c) => c.onBackPressed
        case None => false
      }
      if (!s && getChildFragmentManager.getBackStackEntryCount() > 0) {
        getChildFragmentManager.popBackStack
        s = true
      }
      s
    }
    noStack
  }

  protected def error(tag: String, resId: Int, e: PixivException) {
    error(tag: String, getActivity.getString(resId), e: PixivException)
  }

  protected def error(tag: String, message: String, e: PixivException) {
    e.errorType match {
      case IOError =>
        Log.w(tag, e)
      case t =>
        Log.e(tag, message, e)
    }
    ToastMaster.makeText(getActivity, message, Toast.LENGTH_SHORT)
  }
}
package org.yotchang4s.yapix

import android.support.v4.app.Fragment
import org.yotchang4s.pixiv.PixivException
import org.yotchang4s.pixiv.PixivException._
import android.util.Log
import org.yotchang4s.android.ToastMaster
import android.widget.Toast

abstract class AbstractFragment extends Fragment {
  def onBackPressed: Boolean = false

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
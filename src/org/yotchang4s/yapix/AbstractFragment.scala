package org.yotchang4s.yapix

import android.support.v4.app.Fragment
import org.yotchang4s.pixiv.PixivException
import org.yotchang4s.pixiv.PixivException._
import android.util.Log
import org.yotchang4s.android.ToastMaster
import android.widget.Toast

abstract class AbstractFragment extends Fragment {
  def onBackPressed: Boolean = false

  protected def error(tag: String, e: PixivException) {
    e.errorType match {
      case IOError =>
        Log.w(tag, e.getMessage, e)
        ToastMaster.makeText(getActivity, "接続に失敗しました", Toast.LENGTH_SHORT)
      case t =>
        Log.e(tag, null, e)
        ToastMaster.makeText(getActivity, "不明なエラーです", Toast.LENGTH_SHORT)
    }
  }
}
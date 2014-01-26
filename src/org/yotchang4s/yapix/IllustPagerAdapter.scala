package org.yotchang4s.yapix

import android.support.v4.app.FragmentManager
import org.yotchang4s.pixiv.illust.Illust
import android.support.v4.app.FragmentStatePagerAdapter
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import scala.collection._
import android.util.Log
import android.util.LruCache

class IllustPagerAdapter(fm: FragmentManager) extends ListFragmentStatePagerAdapter(fm) {
  private val TAG = getClass.getSimpleName

  def getItem(position: Int) = {
    Log.i(TAG, "create fragment: position=" + position)
    val illustFragment = new IllustFragment

    val bundle = new Bundle
    bundle.putSerializable(ArgumentKeys.Illust, getIllustList(position))

    illustFragment.setArguments(bundle)

    illustFragment
  }
}
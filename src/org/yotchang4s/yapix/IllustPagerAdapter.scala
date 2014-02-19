package org.yotchang4s.yapix

import scala.collection._
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.util.Log
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import org.yotchang4s.pixiv.illust.Illust

class IllustPagerAdapter(fm: FragmentManager) extends ListFragmentStatePagerAdapter(fm) {
  private val TAG = getClass.getSimpleName
  private val fragmentCache = mutable.Map[Int, Fragment]()

  def getItem(position: Int) = {
    Log.i(TAG, "create fragment: position=" + position)

    val illustFragment = new IllustFragment

    val bundle = new Bundle
    bundle.putSerializable(ArgumentKeys.Illust, getIllustList(position))

    illustFragment.setArguments(bundle)

    fragmentCache.put(position, illustFragment)

    illustFragment
  }

  def getFragment(position: Int): Fragment = {
    fragmentCache(position)
  }

  override def destroyItem(container: View, position: Int, obj: Object) {
    fragmentCache.remove(position)
  }
}
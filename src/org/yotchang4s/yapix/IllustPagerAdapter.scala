package org.yotchang4s.yapix

import android.support.v4.app.FragmentManager
import org.yotchang4s.pixiv.illust.Illust
import android.support.v4.app.FragmentStatePagerAdapter
import android.os.Bundle

class IllustPagerAdapter(fm: FragmentManager) extends FragmentStatePagerAdapter(fm) {
  private[this] var list: List[Illust] = Nil

  def setList(list: List[Illust]) {
    this.list = list
  }

  def getItem(position: Int) = {
    val illustFragment = new IllustFragment

    val bundle = new Bundle
    bundle.putSerializable(ArgumentKeys.Illust, list(position))

    illustFragment.setArguments(bundle)
    
    illustFragment
  }

  def getCount = list.size
}
package org.yotchang4s.yapix

import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.app.FragmentManager
import org.yotchang4s.pixiv.illust.Illust
import android.os.Parcelable

abstract class ListFragmentStatePagerAdapter(fm: FragmentManager) extends FragmentStatePagerAdapter(fm) {
  private[this] var illustList: List[Illust] = Nil
  private[this] var manga: Boolean = false

  def setManga(manga: Boolean) {
    this.manga = manga
  }

  def isManga = manga

  def setIllustList(illustList: List[Illust]) {
    this.illustList = if (illustList == null) Nil else illustList
  }

  def getIllustList = illustList

  def getCount = illustList.size
}
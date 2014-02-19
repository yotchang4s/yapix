package org.yotchang4s.yapix

import android.view._
import android.os.Bundle
import com.android.volley.toolbox.NetworkImageView
import android.support.v4.view.ViewPager
import android.support.v4.app.Fragment
import org.yotchang4s.pixiv.illust._
import android.view.View.OnKeyListener
import org.yotchang4s.yapix.YapixConfig._
import android.util.Log
import org.yotchang4s.android._
import android.widget.Toast
import org.yotchang4s.pixiv._
import scala.concurrent._
import ExecutionContext.Implicits.global
import org.yotchang4s.pixiv.illust._
import org.yotchang4s.pixiv.PixivException._

class IllustViewPagerFragment extends AbstractFragment {
  private val TAG = getClass.getSimpleName

  private[this] var illustList: List[Illust] = null
  private[this] var illustPosition: Int = 0

  private[this] var viewPager: ViewPager = null
  private[this] var adapter: IllustPagerAdapter = null

  setRetainInstance(true)

  override protected def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val viewGroup = inflater.inflate(R.layout.illust_pager_fragment, container, false)
    viewPager = viewGroup.findViewById(R.id.illustPager).asInstanceOf[ViewPager]
    viewGroup
  }

  protected override def onActivityCreated(savedInstanceState: Bundle) {
    super.onActivityCreated(savedInstanceState)

    adapter = new IllustPagerAdapter(getFragmentManager)

    getArguments.getSerializable(ArgumentKeys.IllustList) match {
      case i: Array[_] =>
        illustList = i.map(_.asInstanceOf[Illust]).toList
        illustPosition = getArguments.getInt(ArgumentKeys.IllustListPosition, 0)

        adapter.setIllustList(illustList)
        viewPager.setAdapter(adapter)
        viewPager.setCurrentItem(illustPosition)

      case null =>
        viewPager.setAdapter(adapter)
        getArguments.getSerializable(ArgumentKeys.IllustDetail) match {
          case i: IllustDetail if (i.manga) => asyncDetalAndManga(i)
          case _ => error(new PixivException(UnknownError))
        }
      case _ => error(new PixivException(UnknownError))
    }
  }

  private def asyncDetalAndManga(illust: Illust) {
    val f = future {
      illust.detail
    }

    f.onSuccess {
      case Right(d) => if (d.manga) asyncManga(d.identity) else d
      case Left(e) => error(e)
    }(new UIExecutionContext)
  }

  private def asyncManga(illustId: IllustId) {
    val f = future {
      Pixiv.manga.findMangaBy(illustId)
    }

    f.onSuccess {
      case Right(ms) => setMangaList(ms)
      case Left(e) => error(e)
    }(new UIExecutionContext)
  }

  private def setMangaList(mangaList: List[IllustDetail]) {
    adapter.setIllustList(mangaList)
    adapter.notifyDataSetChanged
  }

  protected[yapix] override def onBackPressed = {
    val f = adapter.getFragment(viewPager.getCurrentItem)
    if (f.isInstanceOf[AbstractFragment]) {
      val af = f.asInstanceOf[AbstractFragment]
      childFragment(af)
      super.onBackPressed
    } else {
      false
    }
  }

  private def error(e: PixivException) {
    e.errorType match {
      case IOError =>
        Log.w(TAG, "illust detail error", e)
        ToastMaster.makeText(getActivity, "接続に失敗しました", Toast.LENGTH_SHORT)
      case t =>
        Log.e(TAG, null, e)
        ToastMaster.makeText(getActivity, "不明なエラーです", Toast.LENGTH_SHORT)
    }
  }
}
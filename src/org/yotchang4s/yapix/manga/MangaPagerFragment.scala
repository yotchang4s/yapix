package org.yotchang4s.yapix.manga

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
import org.yotchang4s.android._
import org.yotchang4s.pixiv.illust._
import org.yotchang4s.pixiv.PixivException._
import org.yotchang4s.yapix.AbstractFragment
import org.yotchang4s.yapix.ArgumentKeys
import org.yotchang4s.yapix.R

class MangaPagerFragment extends AbstractFragment {
  private val TAG = getClass.getSimpleName

  private[this] var viewPager: ViewPager = null
  private[this] var adapter: MangaPagerAdapter = null

  setRetainInstance(true)

  override protected def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val viewGroup = inflater.inflate(R.layout.manga_pager_fragment, container, false)
    viewPager = viewGroup.findViewById(R.id.mangaPager).asInstanceOf[ViewPager]
    viewGroup
  }

  protected override def onActivityCreated(savedInstanceState: Bundle) {
    super.onActivityCreated(savedInstanceState)

    adapter = new MangaPagerAdapter(
      getActivity.getApplicationContext,
      { getScreenSize(getActivity) },
      { getStatusBarSize(getActivity) },
      { getActionBarSize(getActivity) })

    getArguments.getSerializable(ArgumentKeys.IllustDetail) match {
      case i: IllustDetail if (i.manga) =>
        asyncManga(i.identity)
      case _ => error(TAG, new PixivException(UnknownError, "Argument is not IllustDetail"))
    }
  }

  private def asyncManga(illustId: IllustId) {
    val f = future {
      Pixiv.manga.findMangaBy(illustId)
    }

    f.onSuccess {
      case Right(ms) => setMangaList(ms)
      case Left(e) => error(TAG, e)
    }(new UIExecutionContext)
  }

  private def setMangaList(mangaList: List[IllustDetail]) {
    mangaList match {
      case Nil =>
        error(TAG, throw new PixivException(UnknownError, "IllustList is Nil"))

      case mx =>
        adapter.setIllusts(mx)
        viewPager.setAdapter(adapter)
    }
  }
}
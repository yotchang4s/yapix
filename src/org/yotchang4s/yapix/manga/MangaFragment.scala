package org.yotchang4s.yapix.manga

import android.view._
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v4.app.Fragment
import org.yotchang4s.pixiv.illust.Illust
import org.yotchang4s.yapix.ArgumentKeys
import org.yotchang4s.yapix.YapixConfig._
import org.yotchang4s.yapix.R
import scala.Array.canBuildFrom
import android.widget.ProgressBar
import org.yotchang4s.pixiv.illust.IllustDetail
import org.yotchang4s.pixiv.PixivException
import org.yotchang4s.pixiv.Pixiv

class MangaFragment extends Fragment {
  private var mangaProgressBar: ProgressBar = null
  private var viewPager: ViewPager = null
  private var adapter: MangaPagerAdapter = null
  private var illust: Illust = null

  override protected def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val viewGroup = inflater.inflate(R.layout.manga_fragment, container, false)

    mangaProgressBar = viewGroup.findViewById(R.id.mangaProgressBar).asInstanceOf[ProgressBar]

    viewPager = viewGroup.findViewById(R.id.mangaPager).asInstanceOf[ViewPager]

    viewGroup
  }
  

  protected override def onActivityCreated(savedInstanceState: Bundle) {
    super.onActivityCreated(savedInstanceState)

    //adapter = new MangaPagerAdapter(getActivity.getApplicationContext, getSta)

    illust = getArguments.get(ArgumentKeys.Illust).asInstanceOf[Illust]

    asyncSetMangaDetailIllustList(illust)
  }

  private def asyncSetMangaDetailIllustList(illust: Illust) {

    mangaProgressBar.setVisibility(View.VISIBLE)

    import scala.concurrent.ExecutionContext.Implicits.global
    import org.yotchang4s.android.UIExecutionContext
    import org.yotchang4s.scala.FutureUtil._
    import org.yotchang4s.scala.FutureUtil._
    val (future, cancel) = cancellableFuture[Either[PixivException, List[IllustDetail]]](future => {
      Pixiv.manga.findMangaBy(illust.identity)
    })

    future.onSuccess {
      case Right(d) =>
        mangaProgressBar.setVisibility(View.GONE)
        viewPager.setVisibility(View.VISIBLE)

        adapter.setIllusts(d)
        adapter.notifyDataSetChanged

      case Left(e) =>
        mangaProgressBar.setVisibility(View.GONE)
      //error(e)

    }(new UIExecutionContext)
  }
}
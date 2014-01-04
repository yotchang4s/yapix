package org.yotchang4s.yapix;

import scala.collection.convert.WrapAsJava._
import java.util.ArrayList
import android.app._
import android.os.Bundle
import android.view._
import android.widget._
import net.simonvt.menudrawer._
import org.yotchang4s.android.Listeners._
import org.yotchang4s.pixiv.ranking._
import org.yotchang4s.pixiv.ranking.RankingComponent
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity

class YapixActivity extends FragmentActivity {

  var menuDrawer: MenuDrawer = null
  var activeViewId = 0;

  val overallFragment = new RankingFragment
  val illustFragment = new RankingFragment
  val mangaFragment = new RankingFragment
  val novelFragment = new RankingFragment

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)

    getActionBar.setDisplayHomeAsUpEnabled(true);

    menuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.BEHIND, Position.LEFT, MenuDrawer.MENU_DRAG_WINDOW);
    menuDrawer.setContentView(R.layout.activity_main);
    menuDrawer.setMenuView(R.layout.menu_drawer);
    menuDrawer.setDrawerIndicatorEnabled(true);
    menuDrawer.setSlideDrawable(R.drawable.ic_drawer);
    //menuDrawer.peekDrawer

    findViewById(R.id.menuDrawerTimeline).onClick { menuDrawerActiveViewChange(_) }
    findViewById(R.id.menuDrawerSearch).onClick { menuDrawerActiveViewChange(_) }
    findViewById(R.id.menuDrawerProfile).onClick { menuDrawerActiveViewChange(_) }

    findViewById(R.id.menuDrawerRankingOverall).onClick {
      changeRankingFragment(overallFragment, Overall, _)
    }
    findViewById(R.id.menuDrawerRankingIllust).onClick {
      changeRankingFragment(illustFragment, Illust, _)
    }
    findViewById(R.id.menuDrawerRankingManga).onClick {
      changeRankingFragment(mangaFragment, Manga, _)
    }
    findViewById(R.id.menuDrawerRankingNovel).onClick {
      changeRankingFragment(novelFragment, Novel, _)
    }

    def changeRankingFragment(rankingFragment: RankingFragment, rankingCategory: RankingCategory, view: View) {
      menuDrawerActiveViewChange(view)
      val bundle = new Bundle
      bundle.putSerializable(RankingFragment.RankingCategoryKey.key, rankingCategory)
      changeFragment(rankingFragment, Some(bundle))
    }

    menuDrawerActiveViewChange(findViewById(R.id.menuDrawerTimeline))

    def menuDrawerActiveViewChange(v: View) {
      menuDrawer.setActiveView(v)
      menuDrawer.closeMenu
      activeViewId = v.getId
    }

    val activeView = findViewById(activeViewId)
    if (activeView != null) {
      menuDrawer.setActiveView(activeView)
    }
  }

  private def changeFragment(fragment: Fragment, bundle: Option[Bundle] = None) {
    val tran = getSupportFragmentManager.beginTransaction
    tran.replace(R.id.content, fragment)
    bundle.foreach(fragment.setArguments)
    tran.commit
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    item.getItemId match {
      case android.R.id.home =>
        menuDrawer.toggleMenu
        true
      case _ => super.onOptionsItemSelected(item)
    }
  }

  override def onBackPressed {
    val drawerState = menuDrawer.getDrawerState
    if (drawerState == MenuDrawer.STATE_OPEN || drawerState == MenuDrawer.STATE_OPENING) {
      menuDrawer.closeMenu
    } else {
      super.onBackPressed
    }
  }
}

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

  val bookmarkFragment = new BookmarkFragment
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

    findViewById(R.id.menuDrawerTimeline).onClicks += { menuDrawerActiveViewChange(_) }
    findViewById(R.id.menuDrawerSearch).onClicks += { menuDrawerActiveViewChange(_) }
    findViewById(R.id.menuDrawerBookmark).onClicks += {
      changeFragment(bookmarkFragment, _)
    }
    findViewById(R.id.menuDrawerProfile).onClicks += { menuDrawerActiveViewChange(_) }

    findViewById(R.id.menuDrawerRankingOverall).onClicks += {
      changeRankingFragment(overallFragment, Overall, _)
    }
    findViewById(R.id.menuDrawerRankingIllust).onClicks += {
      changeRankingFragment(illustFragment, Illust, _)
    }
    findViewById(R.id.menuDrawerRankingManga).onClicks += {
      changeRankingFragment(mangaFragment, Manga, _)
    }
    findViewById(R.id.menuDrawerRankingNovel).onClicks += {
      changeRankingFragment(novelFragment, Novel, _)
    }

    menuDrawerActiveViewChange(findViewById(R.id.menuDrawerTimeline))
  }

  private def changeRankingFragment(rankingFragment: RankingFragment, rankingCategory: RankingCategory, view: View) {
    rankingFragment.getArguments match {
      case null =>
        val bundle = new Bundle
        bundle.putSerializable(ArgumentKeys.RankingCategory, rankingCategory)
        rankingFragment.setArguments(bundle)
      case _ =>
    }

    changeFragment(rankingFragment, view)
  }

  private def changeFragment(fragment: Fragment, view: View) {
    menuDrawerActiveViewChange(view)

    val tran = getSupportFragmentManager.beginTransaction
    tran.replace(R.id.content, fragment)
    tran.commit
  }

  private def menuDrawerActiveViewChange(v: View) {
    menuDrawer.setActiveView(v)
    menuDrawer.closeMenu
    activeViewId = v.getId
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

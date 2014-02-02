package org.yotchang4s.yapix;

import scala.collection.convert.WrapAsJava._
import java.util.ArrayList
import android.os.Bundle
import android.view._
import android.widget._
import net.simonvt.menudrawer._
import org.yotchang4s.android.Listeners._
import org.yotchang4s.pixiv.ranking._
import org.yotchang4s.yapix.ranking._
import android.support.v4.app._
import android.util.Log
import org.yotchang4s.yapix.search.SearchFragment

class YapixActivity extends FragmentActivity {
  private[this] val TAG = getClass.getName

  private[this] var fragment: Fragment = null
  private[this] var menuDrawer: MenuDrawer = null
  private[this] var activeViewId = 0;

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)

    getActionBar.setDisplayHomeAsUpEnabled(true);

    menuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.BEHIND, Position.LEFT, MenuDrawer.MENU_DRAG_WINDOW);
    menuDrawer.setContentView(R.layout.yapix_activity);
    menuDrawer.setMenuView(R.layout.menu_drawer);
    menuDrawer.setDrawerIndicatorEnabled(true);
    menuDrawer.setSlideDrawable(R.drawable.ic_drawer);
    //menuDrawer.peekDrawer

    /*findViewById(R.id.menuDrawerTimeline).onClicks += { v =>
      menuDrawerActiveViewChange(v)
    }*/
    findViewById(R.id.menuDrawerSearch).onClicks += { v =>
      changeFragment(classOf[SearchFragment])
      menuDrawerActiveViewChange(v)
    }
    findViewById(R.id.menuDrawerBookmark).onClicks += { v =>
      changeFragment(classOf[BookmarkFragment])
      menuDrawerActiveViewChange(v)
    }
    /*findViewById(R.id.menuDrawerProfile).onClicks += { v =>
      menuDrawerActiveViewChange(v)
    }*/

    findViewById(R.id.menuDrawerRankingOverall).onClicks += { v =>
      changeFragment(classOf[OverallRankingFragment])
      menuDrawerActiveViewChange(v)
    }
    findViewById(R.id.menuDrawerRankingIllust).onClicks += { v =>
      changeFragment(classOf[IllustRankingFragment])
      menuDrawerActiveViewChange(v)
    }
    findViewById(R.id.menuDrawerRankingManga).onClicks += { v =>
      changeFragment(classOf[MangaRankingFragment])
      menuDrawerActiveViewChange(v)
    }
    /*findViewById(R.id.menuDrawerRankingNovel).onClicks += { v =>
      changeFragment(classOf[NovelRankingFragment])
      menuDrawerActiveViewChange(v)
    }*/

    Option(savedInstanceState) match {
      case Some(s) =>
        val activeFragmentClassName = savedInstanceState.getString(ArgumentKeys.MainFragmentClassName)
        val menuDrawerActiveViewId = savedInstanceState.getInt(ArgumentKeys.MenuDrawerActiveViewId)

        changeFragment(Class.forName(activeFragmentClassName).asInstanceOf[Class[Fragment]])
        menuDrawerActiveViewChange(findViewById(menuDrawerActiveViewId))
      case None =>
        changeFragment(classOf[OverallRankingFragment])
        menuDrawerActiveViewChange(findViewById(R.id.menuDrawerRankingOverall))
    }
  }

  protected override def onSaveInstanceState(savedInstanceState: Bundle) {
    super.onSaveInstanceState(savedInstanceState)

    savedInstanceState.putString(ArgumentKeys.MainFragmentClassName, fragment.getClass.getName)
    savedInstanceState.putInt(ArgumentKeys.MenuDrawerActiveViewId, activeViewId)
  }

  private def changeFragment[T <: Fragment](fragmentClass: Class[T]) {
    Log.i(TAG, "change fragment")
    val fm = getSupportFragmentManager
    val fragment = fm.findFragmentByTag(fragmentClass.getName)

    val tran = fm.beginTransaction

    if (this.fragment != null) {
      //tran.detach(this.fragment)
      tran.hide(this.fragment)
    }
    this.fragment = fragment match {
      case f: Fragment =>
        //tran.attach(f)
        tran.show(f)
        f
      case _ =>
        val f = Fragment.instantiate(this, fragmentClass.getName, null)
        //tran.add(android.R.id.content, f, fragmentClass.getName)
        tran.add(android.R.id.content, f, fragmentClass.getName)
        f
    }

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

  protected override def onBackPressed {
    val callSuper = {
      val drawerState = menuDrawer.getDrawerState
      if (drawerState == MenuDrawer.STATE_OPEN || drawerState == MenuDrawer.STATE_OPENING) {
        menuDrawer.closeMenu
        false

      } else if (fragment != null && fragment.isInstanceOf[AbstractFragment]) {
        !fragment.asInstanceOf[AbstractFragment].onBackPressed
      } else {
        true
      }
    }
    if (callSuper) {
      moveTaskToBack(true)
    }
  }
}

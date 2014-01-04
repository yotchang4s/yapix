package org.yotchang4s.android;

import scala.collection._
import android.app._
import android.app.ActionBar._
import android.text._
import android.view._
import android.widget._
import android.widget.AdapterView._
import android.widget.AbsListView._
import android.widget.CompoundButton._

object Listeners extends Listeners

trait Listeners {
  implicit class ViewOpts(view: View) {
    def onClick(f: View => Unit) {
      view.setOnClickListener(new View.OnClickListener {
        def onClick(v: View): Unit = f(view)
      })
    }
  }

  implicit class EditTextOpts(editText: EditText) {
    def onEditorAction(f: (Int, KeyEvent) => Boolean) {
      editText.setOnEditorActionListener(new TextView.OnEditorActionListener {
        def onEditorAction(textView: TextView, id: Int, keyEvent: KeyEvent): Boolean = f(id, keyEvent)
      })
    }

    def afterTextChanged(f: Editable => Unit) {
      editText.addTextChangedListener(new TextWatcher {
        def beforeTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {
        }

        def onTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {

        }

        def afterTextChanged(editable: Editable) {
          f(editable)
        }
      })
    }
  }

  implicit class ActionBarOpts(actionBar: ActionBar) {
    def onNavigationItemSelected(s: SpinnerAdapter)(f: (Int, Long) => Boolean) {
      actionBar.setListNavigationCallbacks(s, new OnNavigationListener {
        def onNavigationItemSelected(itemPosition: Int, itemId: Long): Boolean = f(itemPosition, itemId)
      })
    }
  }

  private object AdapterViewOpts {
    lazy val onAllListeners = mutable.Map[AdapterView[_], OnAllListener[_]]()

    class OnAllListener[A <: Adapter] extends OnItemSelectedListener with OnItemClickListener {
      lazy val onItemSelecteds = new mutable.ListBuffer[(AdapterView[_], View, Int, Long) => Unit]
      lazy val onNothingSelecteds = new mutable.ListBuffer[(AdapterView[_]) => Unit]
      lazy val onItemClicks = new mutable.ListBuffer[(AdapterView[_], View, Int, Long) => Unit]

      def onItemSelected(parent: AdapterView[_], view: View, position: Int, id: Long) {
        onItemSelecteds.foreach(_(parent, view, position, id))
      }

      def onNothingSelected(parent: AdapterView[_]) {
        onNothingSelecteds.foreach(_(parent))
      }

      def onItemClick(parent: AdapterView[_], view: View, position: Int, id: Long) {
        onItemClicks.foreach(_(parent, view, position, id))
      }
    }

    def getOrCreateOnAllOnItemSelectedListener(adapterView: AdapterView[_]) = {
      val l = onAllListeners.get(adapterView).getOrElse(new OnAllListener)
      onAllListeners(adapterView) = l
      adapterView.setOnItemSelectedListener(l)
      l
    }

    def getOrCreateOnAllOnClickListener(adapterView: AdapterView[_]) = {
      val l = onAllListeners.get(adapterView).getOrElse(new OnAllListener)
      onAllListeners(adapterView) = l
      adapterView.setOnItemClickListener(l)
      l
    }
  }

  implicit class AdapterViewOpts[V <: AdapterView[_ <: Adapter]](listView: AdapterView[_]) {
    import AdapterViewOpts._

    def onItemSelecteds = getOrCreateOnAllOnItemSelectedListener(listView).onItemSelecteds
    def onNothingSelecteds = getOrCreateOnAllOnItemSelectedListener(listView).onNothingSelecteds

    def onItemClicks = getOrCreateOnAllOnClickListener(listView).onItemClicks
  }

  private object AbsListViewOpts {
    lazy val onAllListeners = mutable.Map[AdapterView[_], OnAllListener]()

    class OnAllListener extends OnScrollListener {
      lazy val onScrolls = new mutable.ListBuffer[(AbsListView, Int, Int, Int) => Unit]
      lazy val onScrollStateChangeds = new mutable.ListBuffer[(AbsListView, Int) => Unit]

      def onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
        onScrolls.foreach(_(view, firstVisibleItem, visibleItemCount, totalItemCount))
      }

      def onScrollStateChanged(view: AbsListView, scrollState: Int) {
        onScrollStateChangeds.foreach(_(view, scrollState))
      }
    }

    def getOrCreateOnAllScrollListener(absListView: AbsListView) = {
      val l = onAllListeners.get(absListView).getOrElse(new OnAllListener)
      onAllListeners(absListView) = l
      absListView.setOnScrollListener(l)
      l
    }
  }

  implicit class AbsListViewOpts(absListView: AbsListView) {
    import AbsListViewOpts._

    def onScrolls = getOrCreateOnAllScrollListener(absListView).onScrolls
    def onScrollChanges = getOrCreateOnAllScrollListener(absListView).onScrollStateChangeds
  }
}
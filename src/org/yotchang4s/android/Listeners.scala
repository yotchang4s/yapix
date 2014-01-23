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
import android.view.View.OnClickListener
import android.view.View.OnKeyListener

object Listeners extends Listeners

trait Listeners {

  object ViewOpts {
    lazy val onAllListeners = mutable.Map[View, OnAllListener]()

    class OnAllListener extends OnClickListener with OnKeyListener {
      lazy val onClicks = new mutable.ListBuffer[View => Unit]
      lazy val onKeys = new mutable.ListBuffer[(View, Int, KeyEvent) => Boolean]

      def onClick(v: View) {
        onClicks.foreach(_(v))
      }

      def onKey(view: View, keyCode: Int, event: KeyEvent): Boolean = {
        onKeys.foreach { v =>
          val result = v(view, keyCode, event)
          if (result) return true
        }
        return false
      }
    }

    def getOrCreateOnAllClickListener(view: View) = {
      val l = onAllListeners.get(view).getOrElse(new OnAllListener)
      onAllListeners(view) = l
      view.setOnClickListener(l)
      l
    }

    def getOrCreateOnAllKeyListener(view: View) = {
      val l = onAllListeners.get(view).getOrElse(new OnAllListener)
      onAllListeners(view) = l
      view.setOnKeyListener(l)
      l
    }
  }

  implicit class ViewOpts(view: View) {
    import ViewOpts._

    def onClicks = getOrCreateOnAllClickListener(view).onClicks
    def onKeys = getOrCreateOnAllKeyListener(view).onKeys
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

    def getOrCreateOnAllItemSelectedListener(adapterView: AdapterView[_]) = {
      val l = onAllListeners.get(adapterView).getOrElse(new OnAllListener)
      onAllListeners(adapterView) = l
      adapterView.setOnItemSelectedListener(l)
      l
    }

    def getOrCreateOnAllItemClickListener(adapterView: AdapterView[_]) = {
      val l = onAllListeners.get(adapterView).getOrElse(new OnAllListener)
      onAllListeners(adapterView) = l
      adapterView.setOnItemClickListener(l)
      l
    }
  }

  implicit class AdapterViewOpts[V <: AdapterView[_ <: Adapter]](listView: AdapterView[_]) {
    import AdapterViewOpts._

    def onItemSelecteds = getOrCreateOnAllItemSelectedListener(listView).onItemSelecteds
    def onNothingSelecteds = getOrCreateOnAllItemSelectedListener(listView).onNothingSelecteds
    def onItemClicks = getOrCreateOnAllItemClickListener(listView).onItemClicks
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
    def onScrollStateChangeds = getOrCreateOnAllScrollListener(absListView).onScrollStateChangeds
  }
}
package org.yotchang4s.android

import android.content.Context
import android.view._
import android.widget._

abstract class GridViewSquareDipAdapter[+V <: View](context: Context, squareViewDip: Int, paddingDip: Int) extends BaseAdapter {

  def createView(position: Int, convertView: View, parent: ViewGroup): V

  def getView(position: Int, convertView: View, parent: ViewGroup): View = {
    val view = convertView match {
      case null => createView(position, convertView, parent)
      case x => convertView.asInstanceOf[V]
    }

    val width = dpToPixel(context, squareViewDip)
    val height = dpToPixel(context, squareViewDip)

    val hwidth = parent.getWidth / parent.asInstanceOf[GridView].getNumColumns

    view.setLayoutParams(
      new AbsListView.LayoutParams(
        hwidth - dpToPixel(context, paddingDip),
        hwidth - dpToPixel(context, paddingDip)))

    view
  }
}
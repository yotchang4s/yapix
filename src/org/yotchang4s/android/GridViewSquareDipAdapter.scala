package org.yotchang4s.android

import android.content.Context
import android.view._
import android.widget._

abstract class GridViewSquareDipAdapter[+V <: View](gridView: GridView, squareViewDip: Int, paddingDip: Int) extends BaseAdapter {

  def createView(position: Int, convertView: View, parent: ViewGroup): V

  def getView(position: Int, convertView: View, parent: ViewGroup): View = {
    val view = convertView match {
      case null => createView(position, convertView, parent)
      case x => convertView.asInstanceOf[V]
    }

    val width = dpToPixel(gridView.getContext, squareViewDip)
    val height = dpToPixel(gridView.getContext, squareViewDip)

    val numColumn = parent.getWidth / width
    
    val hwidth = parent.getWidth / gridView.getNumColumns
    view.setLayoutParams(
      new AbsListView.LayoutParams(
        hwidth - dpToPixel(gridView.getContext, paddingDip),
        hwidth - dpToPixel(gridView.getContext, paddingDip)))

    view
  }
}
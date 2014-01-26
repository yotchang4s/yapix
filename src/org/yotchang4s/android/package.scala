package org.yotchang4s

import _root_.android.content.Context
import _root_.android.graphics.Rect
import _root_.android.app.Activity
import _root_.android.util._
import _root_.android.view.WindowManager

package object android {
  def dpToPixel(context: Context, dp: Int): Int = {
    val density = context.getResources.getDisplayMetrics.density;

    (dp.toFloat * density + 0.5f).toInt
  }

  def getStatusBarSize(activity: Activity) = {
    val rect = new Rect
    activity.getWindow.getDecorView.getWindowVisibleDisplayFrame(rect);
    rect.top
  }

  def getActionBarSize(context: Context) = {
    val styledAttributes = context.getTheme.obtainStyledAttributes(Array(_root_.android.R.attr.actionBarSize))
    val abs = styledAttributes.getDimension(0, 0).toInt
    styledAttributes.recycle
    abs
  }

  def getScreenSize(context: Context): (Int, Int) = {
    val wm = context.getSystemService(Context.WINDOW_SERVICE).asInstanceOf[WindowManager]

    val metrics = new DisplayMetrics
    wm.getDefaultDisplay.getMetrics(metrics)

    (metrics.widthPixels, metrics.heightPixels)
  }
}
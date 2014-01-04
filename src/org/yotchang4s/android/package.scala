package org.yotchang4s

import _root_.android.content.Context
import _root_.android.util.TypedValue

package object android {
  def dpToPixel(context: Context, dp: Int): Int = {
    val density = context.getResources.getDisplayMetrics.density;

    (dp.toFloat * density + 0.5f).toInt
  }
}
package org.yotchang4s.android

import android.widget.Toast
import android.content.Context

object ToastMaster {
  private var sToast: Toast = null

  def makeText(context: Context, resId: Int, duration: Int): Toast = {
    val toast = new ToastMaster(context)

    toast.setText(resId)
    toast.setDuration(duration)

    toast
  }

  def makeText(context: Context, text: CharSequence, duration: Int): Toast = {
    val toast = new ToastMaster(context)

    toast.setText(text)
    toast.setDuration(duration)

    toast
  }

  private def setToast(toast: Toast) {
    if (sToast != null) {
      sToast.cancel
    }
    sToast = toast
  }

  private def cancelToast {
    if (sToast != null) {
      sToast.cancel
    }
    sToast = null
  }
}

class ToastMaster(context: Context) extends Toast(context) {

  override def show {
    ToastMaster.setToast(this)
    super.show
  }
}
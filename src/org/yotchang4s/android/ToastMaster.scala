package org.yotchang4s.android

import android.widget.Toast
import android.content.Context

object ToastMaster {
  private var sToast: Toast = null

  def makeText(context: Context, resId: Int, duration: Int): ToastMaster = {
    val toast = Toast.makeText(context, resId, duration)

    new ToastMaster(toast)
  }

  def makeText(context: Context, text: CharSequence, duration: Int): ToastMaster = {
    val toast = Toast.makeText(context, text, duration)

    new ToastMaster(toast)
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

class ToastMaster private (toast: Toast) {
  def show {
    ToastMaster.setToast(toast)
    toast.show
  }
}
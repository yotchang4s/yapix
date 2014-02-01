package org.yotchang4s.yapix.login

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import android.app._
import android.os.Bundle
import android.view._
import android.content.DialogInterface
import android.util.Log

import android.support.v4.app.DialogFragment

import org.yotchang4s.android._
import org.yotchang4s.pixiv.auth._
import org.yotchang4s.yapix._
import org.yotchang4s.yapix.YapixConfig._

class LoggedInDialogFragment extends DialogFragment {

  private val TAG = getClass.getSimpleName

  private[this] var onReturn: Option[(AuthResult => Unit)] = None

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val content = inflater.inflate(R.layout.logged_in_fragment, null)
    content
  }

  override protected def onCreateDialog(savedInstanceState: Bundle): Dialog = {
    val dialog = new Dialog(getActivity, R.style.Yapix_LoggedIn)
    dialog.setCanceledOnTouchOutside(false)
    dialog.setCancelable(false)

    val window = dialog.getWindow
    window.requestFeature(Window.FEATURE_NO_TITLE)
    window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);

    asyncLogin

    dialog
  }

  private def asyncLogin {
    val f = future {
      val auth = new FormAuth
      auth.authcation
    }

    f.onSuccess {
      case s: AuthSuccess =>
        onReturn.foreach(_(s))

      case f: AuthFailure =>
        Log.w(TAG, f.cause getOrElse null)

        onReturn.foreach(_(f))
        dismiss

    }(new UIExecutionContext)
  }

  def onReturn(onReturn: (AuthResult => Unit)) {
    this.onReturn = Option(onReturn)
  }
}
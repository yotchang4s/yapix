package org.yotchang4s.yapix.login

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import android.app._
import android.os.Bundle
import android.util.Log
import android.view._
import android.widget.Toast
import android.support.v4.app.DialogFragment
import org.yotchang4s.android.UIExecutionContext
import org.yotchang4s.pixiv.auth._
import org.yotchang4s.yapix.YapixConfig.yapixConfig
import org.yotchang4s.android.ToastMaster
import android.content.DialogInterface
import org.yotchang4s.yapix.R
import org.yotchang4s.yapix.YapixConfig

object LoggedInDialogFragment {
  trait LoginResult
  case object Ok extends LoginResult
  case object Ng extends LoginResult
}

class LoggedInDialogFragment extends DialogFragment {
  import LoggedInDialogFragment._

  private val TAG = getClass.getSimpleName

  private[this] var _onReturn: Option[(LoginResult => Unit)] = None

  private[this] var loginCancel: Boolean = false

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

  override def onCancel(dialog: DialogInterface) {
    this.loginCancel = true
  }

  private def asyncLogin {
    val f = future {
      val auth = new FormAuth
      auth.authcation
    }

    f.onSuccess {
      case AuthSuccess(pixivId, userId, authToken) => {
        if (!loginCancel) {
          YapixConfig.yapixConfig.userId(userId)
          YapixConfig.yapixConfig.authToken(authToken)

          ToastMaster.makeText(getActivity, "ログインできました", Toast.LENGTH_SHORT).show

          _onReturn.foreach(_(Ok))
        }
      }
      case AuthFailure(msg, e) =>
        Log.w(TAG, msg, e getOrElse null)

        ToastMaster.makeText(getActivity, "ログインに失敗しました\n" + msg, Toast.LENGTH_SHORT).show

        _onReturn.foreach(_(Ng))
        dismiss
    }(new UIExecutionContext)
  }

  def onReturn(onReturn: (LoginResult => Unit)) {
    _onReturn = Option(onReturn)
  }
}
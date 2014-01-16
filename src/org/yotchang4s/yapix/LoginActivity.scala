package org.yotchang4s.yapix

import android.animation._
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.text._
import android.util.Log
import android.view._
import android.view.inputmethod.EditorInfo
import android.widget._
import org.yotchang4s.android._
import org.yotchang4s.android.Listeners._
import org.yotchang4s.pixiv._
import org.yotchang4s.pixiv.auth._
import android.support.v4.app.FragmentActivity
import android.app.ProgressDialog
import android.app.Dialog

class LoginActivity extends FragmentActivity { loginActivity =>
  private[this] val TAG = getClass.getName

  private[this] val PROGRESS_DIALOG = 1

  private[this] var progressDialog: ProgressDialog = null

  protected override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.activity_login)

    val loginPixivIdEditText = findViewById(R.id.login_pixiv_id).asInstanceOf[EditText]
    val loginPixivPasswordEditText = findViewById(R.id.login_pixiv_password).asInstanceOf[EditText]
    val loginButton = findViewById(R.id.login_button)

    for {
      id <- YapixConfig.yapixConfig.pixivId
      ps <- YapixConfig.yapixConfig.pixivPassword
    } {
      loginPixivIdEditText.setText(id)
      loginPixivPasswordEditText.setText(ps)

      checkFieldsForEmptyValues
      authcation
    }

    loginPixivIdEditText.afterTextChanged { _ =>
      checkFieldsForEmptyValues
    }

    loginPixivPasswordEditText.afterTextChanged { _ =>
      checkFieldsForEmptyValues
    }

    def checkFieldsForEmptyValues {
      val pixivId = loginPixivIdEditText.getText.toString;
      val pixivPassword = loginPixivPasswordEditText.getText.toString;

      if ("".equals(pixivId) || "".equals(pixivPassword)) {
        loginButton.setEnabled(false);
      } else {
        loginButton.setEnabled(true);
        YapixConfig.yapixConfig.pixivId(pixivId)
        YapixConfig.yapixConfig.pixivPassword(pixivPassword)
      }
    }

    loginButton.onClicks += { _ =>
      authcation
    }
  }

  private def authcation {
    val loginTask = new LoginTask
    loginTask.execute(None)
  }

  private def moveYapixActivity {
    val intent = new Intent(getApplicationContext, classOf[YapixActivity])
    startActivity(intent);
    overridePendingTransition(0, 0);
  }

  protected override def onCreateDialog(id: Int): Dialog = {
    val dialog = super.onCreateDialog(id)
    id match {
      case PROGRESS_DIALOG =>
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("ログイン中");
        progressDialog.setMessage("お待ちください");
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog

      case _ => dialog
    }
  }

  protected override def onPause {
    super.onPause

    if (progressDialog != null) {
      progressDialog = null;
      dismissDialog(PROGRESS_DIALOG) // ダイアログのグルグルを終了
    }
  }

  class LoginTask extends AsyncTask[Option[Nothing], Option[Nothing], AuthResult] {

    protected override def onPreExecute {

      showDialog(PROGRESS_DIALOG)
    }

    protected override def doInBackground(params: Option[Nothing]): AuthResult = {
      import YapixConfig._

      val auth = new FormAuth
      auth.authcation
    }

    protected override def onPostExecute(result: AuthResult) {
      result match {
        case AuthSuccess(pixivId, userId, authToken) => {
          YapixConfig.yapixConfig.userId(userId)
          YapixConfig.yapixConfig.authToken(authToken)

          Toast.makeText(loginActivity, "ログインできました", Toast.LENGTH_LONG).show;
          moveYapixActivity
        }
        case AuthFailure(msg, e) =>
          Log.w(TAG, msg, e getOrElse null)
          Toast.makeText(loginActivity, "ログインに失敗しました\n" + msg, Toast.LENGTH_LONG).show;
      }

      dismissDialog(PROGRESS_DIALOG)
    }
  }
}

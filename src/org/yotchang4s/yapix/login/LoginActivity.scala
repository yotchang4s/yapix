package org.yotchang4s.yapix.login

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
import org.yotchang4s.yapix.YapixActivity
import org.yotchang4s.yapix.YapixConfig
import org.yotchang4s.yapix.R

class LoginActivity extends FragmentActivity { loginActivity =>
  private[this] val TAG = getClass.getName

  private[this] val PROGRESS_DIALOG = 1

  private[this] var progressDialog: ProgressDialog = null

  protected override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.login_fragment)

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
    import LoggedInDialogFragment._

    val dialog = new LoggedInDialogFragment
    dialog.onReturn { r =>
      r match {
        case Ok =>
          moveYapixActivity
        case Ng =>
      }
    }
    dialog.show(getSupportFragmentManager, "Login")
  }

  private def moveYapixActivity {
    val intent = new Intent(getApplicationContext, classOf[YapixActivity])
    startActivity(intent);
    overridePendingTransition(0, 0);
  }
}

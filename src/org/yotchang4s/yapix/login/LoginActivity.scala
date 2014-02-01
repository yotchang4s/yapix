package org.yotchang4s.yapix.login

import android.os.Bundle
import android.content.Intent
import android.widget._

import android.support.v4.app.FragmentActivity

import org.yotchang4s.android._
import org.yotchang4s.android.Listeners._
import org.yotchang4s.yapix._
import org.yotchang4s.yapix.YapixConfig
import org.yotchang4s.pixiv.auth._

class LoginActivity extends FragmentActivity { loginActivity =>
  private[this] val TAG = getClass.getName

  private[this] val PROGRESS_DIALOG = 1

  private[this] var pixivId: Option[String] = None
  private[this] var pixivPassword: Option[String] = None

  protected override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.login_activity)

    val loginPixivIdEditText = findViewById(R.id.login_pixiv_id).asInstanceOf[EditText]
    val loginPixivPasswordEditText = findViewById(R.id.login_pixiv_password).asInstanceOf[EditText]
    val loginButton = findViewById(R.id.login_button)

    for {
      id <- pixivId
      ps <- pixivPassword
    } {
      loginPixivIdEditText.setText(id)
      loginPixivPasswordEditText.setText(ps)

      checkFieldsForEmptyValues
      authcation

      return
    }

    loginPixivIdEditText.afterTextChanged { _ =>
      checkFieldsForEmptyValues
    }

    loginPixivPasswordEditText.afterTextChanged { _ =>
      checkFieldsForEmptyValues
    }

    checkFieldsForEmptyValues

    loginButton.onClicks += { _ =>
      authcation
    }

    def checkFieldsForEmptyValues {
      val pixivId = loginPixivIdEditText.getText.toString;
      val pixivPassword = loginPixivPasswordEditText.getText.toString;

      if ("".equals(pixivId) || "".equals(pixivPassword)) {
        loginButton.setEnabled(false);
      } else {
        loginButton.setEnabled(true);

      }
    }
  }

  private def authcation {

    val dialog = new LoggedInDialogFragment
    dialog.onReturn { r =>
      r match {
        case s: AuthSuccess =>
          YapixConfig.yapixConfig.pixivId(s.pixivId)
          YapixConfig.yapixConfig.pixivPassword(s.pivixPassword)

          ToastMaster.makeText(this, getString(R.string.loggedSuccess), Toast.LENGTH_SHORT).show

          moveYapixActivity

        case f: AuthFailure =>

          ToastMaster.makeText(this, getString(R.string.loggedFailure) + "\n" + f.reasonMessage, Toast.LENGTH_SHORT).show
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

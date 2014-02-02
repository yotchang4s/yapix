package org.yotchang4s.yapix.login

import android.content.Intent
import android.os.Bundle
import android.widget._

import android.support.v4.app.FragmentActivity

import org.yotchang4s.android._
import org.yotchang4s.android.Listeners._
import org.yotchang4s.pixiv.auth._
import org.yotchang4s.yapix._
import org.yotchang4s.yapix.YapixConfig

class LoginActivity extends FragmentActivity {

  private[this] val TAG = getClass.getName

  private[this] var _loginPixivIdEditText: Option[EditText] = None
  private[this] var _loginPixivPasswordEditText: Option[EditText] = None

  protected override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.login_activity)

    val loginPixivIdEditText = findViewById(R.id.login_pixiv_id).asInstanceOf[EditText]
    val loginPixivPasswordEditText = findViewById(R.id.login_pixiv_password).asInstanceOf[EditText]

    _loginPixivIdEditText = Some(loginPixivIdEditText)
    _loginPixivPasswordEditText = Some(loginPixivPasswordEditText)

    val loginButton = findViewById(R.id.login_button)

    for {
      pixivId <- YapixConfig.yapixConfig.pixivId
      pixivPassword <- YapixConfig.yapixConfig.pixivPassword
    } {
      loginPixivIdEditText.setText(pixivId)
      loginPixivPasswordEditText.setText(pixivPassword)

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
          YapixConfig.yapixConfig.authToken(s.authToken)

          ToastMaster.makeText(this, getString(R.string.loggedSuccess), Toast.LENGTH_SHORT).show

          moveYapixActivity

        case f: AuthFailure =>

          ToastMaster.makeText(this, getString(R.string.loggedFailure) + "\n" + f.reasonMessage, Toast.LENGTH_SHORT).show
      }
    }
    for {
      loginPixivIdEditText <- _loginPixivIdEditText
      loginPixivPasswordEditText <- _loginPixivPasswordEditText
    } {
      val arguments = Option(dialog.getArguments).getOrElse(new Bundle)
      arguments.putString(ArgumentKeys.PixivId, loginPixivIdEditText.getText.toString)
      arguments.putString(ArgumentKeys.PixivPassword, loginPixivPasswordEditText.getText.toString)
      dialog.setArguments(arguments)

      dialog.show(getSupportFragmentManager, "Login")
    }
  }

  private def moveYapixActivity {
    val intent = new Intent(getApplicationContext, classOf[YapixActivity])
    startActivity(intent);
    overridePendingTransition(0, 0);
  }
}

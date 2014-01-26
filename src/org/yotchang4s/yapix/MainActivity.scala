package org.yotchang4s.yapix;

import android.os.Bundle
import android.content.Intent
import android.support.v4.app.FragmentActivity
import org.yotchang4s.yapix.login.LoginActivity

class MainActivity extends FragmentActivity {
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)

    val intent = new Intent(getApplicationContext, classOf[LoginActivity]);
    startActivity(intent);
    // 画面遷移時のアニメーションを消す
    overridePendingTransition(0, 0);
  }
}

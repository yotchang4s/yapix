package org.yotchang4s.yapix;

import android.os.Bundle
import android.app.Activity
import android.view._
import org.yotchang4s.android._
import android.widget._
import scala.collection.convert.WrapAsJava._
import android.app.ActionBar
import android.app.Fragment
import android.content.Intent

class MainActivity extends Activity {
  val PREFERENCES_FILE_NAME = "preference"

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)

    val intent = new Intent(getApplicationContext, classOf[LoginActivity]);
    startActivity(intent);
    // 画面遷移時のアニメーションを消す
    overridePendingTransition(0, 0);
  }
}

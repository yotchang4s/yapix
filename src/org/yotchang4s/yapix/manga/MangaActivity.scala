package org.yotchang4s.yapix.manga

import android.support.v4.app.FragmentActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.content.Intent
import org.yotchang4s.pixiv.illust.Illust
import scala.concurrent._
import ExecutionContext.Implicits.global
import org.yotchang4s.android.UIExecutionContext
import org.yotchang4s.yapix.YapixConfig._
import org.yotchang4s.android.ToastMaster
import android.util.Log
import android.widget.Toast
import org.yotchang4s.pixiv.illust.IllustDetail
import org.yotchang4s.yapix.R
import org.yotchang4s.yapix.ArgumentKeys

class MangaActivity extends FragmentActivity {
  private val TAG = getClass.getSimpleName

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.manga_activity)

    val intent = getIntent();
    if (intent == null) {
      finish
    }
    val illust = intent.getSerializableExtra(ArgumentKeys.IllustDetail)
    if (illust == null || !illust.isInstanceOf[Illust]) {
      finish
    }
    asyncDetal(illust.asInstanceOf[Illust])
  }

  private def asyncDetal(illust: Illust) {
    val f = future {
      illust.detail
    }
    f onSuccess {
      case Right(d) => changeMangaFragment(d)
      case Left(e) =>
        error(e)
        finish
    }
  }

  private def changeMangaFragment(illustDetail: IllustDetail) {
    val bundle = new Bundle
    bundle.putSerializable(ArgumentKeys.IllustDetail, illustDetail)
    val f = {
      val clazz =
        if (illustDetail.manga) classOf[MangaPagerFragment]
        else classOf[MangaPagerFragment]

      Fragment.instantiate(this, clazz.getName, bundle)
    }

    val tran = getSupportFragmentManager.beginTransaction
    tran.add(R.id.mangaContent, f)
    tran.commit
  }

  private def error(e: Exception) {
    ToastMaster.makeText(this, "接続に失敗しました", Toast.LENGTH_LONG).show
    Log.w(TAG, "接続に失敗しました", e)
  }
}
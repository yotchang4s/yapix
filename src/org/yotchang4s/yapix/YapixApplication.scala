package org.yotchang4s.yapix

import android.app.Application
import android.preference.PreferenceManager

class YapixApplication extends Application {
  override def onCreate {
    YapixConfig.sharedPreferences(PreferenceManager.getDefaultSharedPreferences(this));
    ImageCacheManager.context(this)
  }

  override def onTerminate {
    YapixConfig.sharedPreferences(null);
  }
}
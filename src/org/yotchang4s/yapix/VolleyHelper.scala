package org.yotchang4s.yapix

import java.io.File
import android.content.Context
import com.android.volley.toolbox.HttpStack
import com.android.volley.RequestQueue
import android.content.pm.PackageManager.NameNotFoundException
import android.os.Build
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.HttpClientStack
import android.net.http.AndroidHttpClient
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache

object VolleyHelper {
  val DEFAULT_CACHE_DIR = "volley"

  def newRequestQueue(context: Context, cacheSize: Int): RequestQueue = {
    val cacheDir = new File(context.getCacheDir, DEFAULT_CACHE_DIR)
    val stack = new HurlStack
    val network = new BasicNetwork(stack)
    val queue = new RequestQueue(new DiskBasedCache(cacheDir, cacheSize), network)

    queue.start
    queue
  }
}
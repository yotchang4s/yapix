package org.yotchang4s.yapix

import android.content.Context
import com.android.volley.RequestQueue
import android.app.ActivityManager
import java.io.File
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.ImageLoader.ImageCache
import android.util.LruCache
import android.graphics.Bitmap
import android.annotation.TargetApi
import android.os.Build

object ImageCacheManager {
  private val diskCacheSize = 32 * 1024 * 1024

  private var _context: Option[Context] = None
  private var _memoryCacheSize: Option[Int] = None
  private var _cacheDir: Option[File] = None

  private var _requestQueue: Option[RequestQueue] = None
  private var _imageLoader: Option[ImageLoader] = None

  def context: Option[Context] = _context
  def context(context: Context) = {
    _context = Option(context)

    _memoryCacheSize = None
    _cacheDir = None

    _requestQueue = None
    _imageLoader = None

    _context.foreach { c =>

      val memClass = getMemLargeClass(c)

      _memoryCacheSize = Some(1024 * 1024 * memClass / 8)
      _cacheDir = Some(new File(c.getCacheDir, "thumbs"))
      _cacheDir.get.mkdir
    }
  }

  private def getMemLargeClass(context: Context) = {
    val am = context.getSystemService(Context.ACTIVITY_SERVICE).asInstanceOf[ActivityManager]

    am.getLargeMemoryClass
  }

  def requestQueue = {
    _requestQueue match {
      case q: Some[RequestQueue] => q
      case None =>
        _requestQueue = for (c <- context) yield {
          VolleyHelper.newRequestQueue(c, diskCacheSize)
        }
        _requestQueue
    }
  }

  def imageLoader = {
    _imageLoader match {
      case i: Some[ImageLoader] => i
      case None =>
        _imageLoader = for {
          s <- _memoryCacheSize
          q <- requestQueue
        } yield {
          new ImageLoader(q, new BitmapImageCache(s));
        }
        _imageLoader
    }
  }

  private class BitmapImageCache(maxSize: Int) extends ImageCache {

    private val mCache = new LruCache[String, Bitmap](maxSize) {
      override def sizeOf(key: String, value: Bitmap): Int = {
        return value.getRowBytes * value.getHeight
      }
    }

    def getBitmap(url: String): Bitmap = {
      mCache.get(url);
    }

    def putBitmap(url: String, bitmap: Bitmap) {
      mCache.put(url, bitmap);
    }
  }
}
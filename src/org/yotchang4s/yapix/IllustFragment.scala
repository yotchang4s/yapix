package org.yotchang4s.yapix

import android.os.Bundle
import org.yotchang4s.pixiv.illust._
import android.view._
import android.support.v4.app.Fragment
import org.yotchang4s.yapix.YapixConfig._
import org.yotchang4s.pixiv.PixivException
import org.yotchang4s.pixiv.Config
import org.yotchang4s.android.ToastMaster
import android.widget.Toast
import org.yotchang4s.android.UIExecutionContext
import org.yotchang4s.yapix.volley.ImageListenerJava
import org.yotchang4s.yapix.volley.ImageContainerJava
import com.android.volley.VolleyError
import android.util.Log
import android.widget.ImageView
import android.graphics.Bitmap
import android.graphics.Matrix
import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue
import android.graphics.Rect

class IllustFragment extends Fragment { self =>
  private val TAG = getClass.getSimpleName

  private[this] var illust: Illust = null

  private[this] var netWorkImageView: ImageView = null
  private[this] var isGetMiddleImage = false
  private[this] var viewGroup: View = null

  private var actionBarSize: Int = 0
  private var statusBarHeight: Int = 0

  private var windowManager: WindowManager = null

  protected override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)

    illust = getArguments.get(ArgumentKeys.Illust).asInstanceOf[Illust]
  }

  override protected def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    viewGroup = inflater.inflate(R.layout.illust_fragment, container, false)

    netWorkImageView = viewGroup.findViewById(R.id.illust).asInstanceOf[ImageView]

    viewGroup
  }

  override protected def onActivityCreated(savedInstanceState: Bundle) {
    super.onActivityCreated(savedInstanceState)

    actionBarSize = {
      val styledAttributes = getActivity.getTheme.obtainStyledAttributes(
        Array(android.R.attr.actionBarSize))
      val abs = styledAttributes.getDimension(0, 0).toInt
      styledAttributes.recycle
      abs
    }

    statusBarHeight = {
      val rect = new Rect
      val window = getActivity.getWindow
      window.getDecorView.getWindowVisibleDisplayFrame(rect);
      rect.top
    }

    windowManager = getActivity.getApplicationContext.getSystemService(Context.WINDOW_SERVICE).asInstanceOf[WindowManager]

    asyncGetImageAndThenSetImageBitmap(illust)
    illust match {
      case d: IllustDetail =>
        asyncGetImageAndThenSetImageBitmap(d)
      case i =>
        asyncGetDetailAndThenSetBitmapImage(illust, YapixConfig.yapixConfig)
    }
  }

  private def asyncGetDetailAndThenSetBitmapImage(illust: Illust, config: Config) {
    import scala.concurrent.ExecutionContext.Implicits.global
    import org.yotchang4s.scala.FutureUtil._
    val (future, cancel) = cancellableFuture[Either[PixivException, IllustDetail]](future => {
      illust.detail(config)
    })

    future.onSuccess {
      case Right(d) =>
        asyncGetImageAndThenSetImageBitmap(d)
      case Left(e) =>
        error(e)

    }(new UIExecutionContext)
  }

  private def setImageBitmap(bitmap: Bitmap) {
    if (bitmap == null) {
      return ;
    }
    val srcWidth = bitmap.getWidth
    val srcHeight = bitmap.getHeight

    val metrics = new DisplayMetrics
    windowManager.getDefaultDisplay.getMetrics(metrics)

    val screenWidth = metrics.widthPixels.toFloat
    val screenHeight = (metrics.heightPixels - actionBarSize - statusBarHeight).toFloat

    val widthScale = screenWidth / srcWidth;
    val heightScale = screenHeight / srcHeight;

    val newMatrix = new Matrix
    val values = new Array[Float](9)

    newMatrix.getValues(values)

    if (widthScale > heightScale) {
      values(Matrix.MSCALE_X) = heightScale
      values(Matrix.MSCALE_Y) = heightScale
    } else {
      values(Matrix.MSCALE_X) = widthScale
      values(Matrix.MSCALE_Y) = widthScale
    }

    newMatrix.setValues(values)

    val resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, srcWidth, srcHeight, newMatrix, true)
    netWorkImageView.setImageBitmap(resizedBitmap)

    val resizeAfterMatrix = netWorkImageView.getImageMatrix

    resizeAfterMatrix.getValues(values)
    values(Matrix.MTRANS_X) = (screenWidth - resizedBitmap.getWidth) / 2
    resizeAfterMatrix.setValues(values)
  }

  private def asyncGetImageAndThenSetImageBitmap(illust: Illust) {

    val (url, isMiddleImage) = illust match {
      case d: IllustDetail => (d.middleImageUrl, true)
      case i => (i.thumbnailImageUrl, false)
    }

    Log.i(TAG, url)
    ImageCacheManager.imageLoader.foreach {
      _.get(url, new ImageListenerJava {
        def onResponse(response: ImageContainerJava, isImmediate: Boolean) {
          synchronized {
            if (!isMiddleImage && isGetMiddleImage) {
              return
            }

            // netWorkImageView.setImageBitmap(response.getBitmap)
            setImageBitmap(response.getBitmap())
            if (isMiddleImage) {
              isGetMiddleImage = true
            }
          }
        }

        def onErrorResponse(e: VolleyError) {
          error(e)
        }
      })
    }
  }

  private def error(e: Exception) {
    ToastMaster.makeText(getActivity, "接続に失敗しました", Toast.LENGTH_LONG).show
    Log.w(TAG, "接続に失敗しました", e)
  }
}
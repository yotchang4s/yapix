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
  private[this] val TAG = getClass.getSimpleName

  private var illust: Illust = null

  private var netWorkImageView: ImageView = null
  private var isGetMiddleImage = false
  private var viewGroup: View = null

  protected override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)

    illust = getArguments.get(ArgumentKeys.Illust).asInstanceOf[Illust]
  }

  override protected def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    viewGroup = inflater.inflate(R.layout.illust_fragment, container, false)

    netWorkImageView = viewGroup.findViewById(R.id.illust).asInstanceOf[ImageView]
    setBitmapImage(illust)

    viewGroup
  }

  override protected def onActivityCreated(savedInstanceState: Bundle) {
    super.onActivityCreated(savedInstanceState)

    illust match {
      case d: IllustDetail =>
        setBitmapImage(d)
      case i =>
        getDetail(illust, YapixConfig.yapixConfig)
    }
  }

  private def getDetail(illust: Illust, config: Config) {
    import scala.concurrent.ExecutionContext.Implicits.global
    import org.yotchang4s.scala.FutureUtil._
    val (future, cancel) = cancellableFuture[Either[PixivException, IllustDetail]](future => {
      illust.detail(config)
    })

    future.onSuccess {
      case Right(d) =>
        setBitmapImage(d)
      case Left(e) =>
        error(e)

    }(new UIExecutionContext())
  }

  private def setImageBitmap(bitmap: Bitmap) {
    if (bitmap == null) {
      return ;
    }
    val srcWidth = bitmap.getWidth(); // 元画像のwidth
    val srcHeight = bitmap.getHeight(); // 元画像のheight

    val styledAttributes = getActivity.getTheme().obtainStyledAttributes(
      Array(android.R.attr.actionBarSize));
    val actionBarSize = styledAttributes.getDimension(0, 0).toInt
    styledAttributes.recycle();

    val rect = new Rect
    val window = getActivity.getWindow
    window.getDecorView().getWindowVisibleDisplayFrame(rect);
    val statusBarHeight = rect.top;

    val metrics = new DisplayMetrics();
    getActivity.getApplicationContext.getSystemService(Context.WINDOW_SERVICE).asInstanceOf[WindowManager]
      .getDefaultDisplay.getMetrics(metrics)

    val screenWidth = metrics.widthPixels.toFloat
    val screenHeight = (metrics.heightPixels - actionBarSize - statusBarHeight).toFloat;

    val widthScale = screenWidth / srcWidth;
    val heightScale = screenHeight / srcHeight;

    val matrix = new Matrix();
    if (widthScale > heightScale) {
      matrix.postScale(heightScale, heightScale);
    } else {
      matrix.postScale(widthScale, widthScale);
    }
    val resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, srcWidth, srcHeight, matrix, true);

    netWorkImageView.setImageBitmap(resizedBitmap)
  }

  private def setBitmapImage(illust: Illust) {

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
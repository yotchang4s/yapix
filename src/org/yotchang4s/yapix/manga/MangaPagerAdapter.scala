package org.yotchang4s.yapix.manga

import org.yotchang4s.pixiv.illust.Illust
import android.support.v4.view.PagerAdapter
import android.view.ViewGroup
import android.view.View
import android.content.Context
import com.android.volley.toolbox.NetworkImageView
import org.yotchang4s.pixiv.illust.IllustDetail
import org.yotchang4s.pixiv.PixivException
import org.yotchang4s.yapix.YapixConfig._
import org.yotchang4s.android._
import android.util.Log
import org.yotchang4s.yapix.volley.ImageListenerJava
import org.yotchang4s.yapix.volley.ImageContainerJava
import com.android.volley.VolleyError
import android.graphics.Bitmap
import android.util.DisplayMetrics
import android.graphics.Matrix
import android.view.WindowManager
import android.app.Activity
import android.graphics.Rect
import org.yotchang4s.android.UIExecutionContext
import org.yotchang4s.scala.FutureUtil.cancellableFuture
import org.yotchang4s.yapix._
import org.yotchang4s.android._

import scala.concurrent.ExecutionContext.Implicits.global
import android.widget.ImageView

class MangaPagerAdapter(
  context: Context,
  screenSize: => (Int, Int),
  statusBarSize: => Int,
  actionBarSize: => Int) extends PagerAdapter {

  private[this] val TAG = getClass.getName

  private[this] val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)

  private var illusts: List[Illust] = Nil

  def setIllusts(illusts: List[Illust]) {
    this.illusts = if (illusts == null) Nil else illusts
  }

  override def instantiateItem(container: ViewGroup, position: Int): Object = {
    val networkImageView = new ImageView(context)

    asyncSetIllustDetail(networkImageView, illusts(position))

    container.addView(networkImageView)

    networkImageView
  }

  private def asyncSetIllustDetail(imageView: ImageView, illust: Illust) {
    //illustDetailProgressBar.setVisibility(View.VISIBLE)

    import scala.concurrent.ExecutionContext.Implicits.global
    import org.yotchang4s.scala.FutureUtil._
    import org.yotchang4s.android.UIExecutionContext
    val (future, cancel) = cancellableFuture[Either[PixivException, IllustDetail]](future => {
      illust.detail
    })

    future.onSuccess {
      case Right(d) =>
        //illustDetailProgressBar.setVisibility(View.GONE)
        setImageUrl(d.middleImageUrl, imageView)

      case Left(e) =>
        //illustDetailProgressBar.setVisibility(View.GONE)
        Log.w(TAG, e)
      //error(e)

    }(new UIExecutionContext)
  }

  override def destroyItem(container: ViewGroup, position: Int, obj: Object) {
    container.removeView(obj.asInstanceOf[View])
  }

  override def getCount: Int = illusts.size

  override def isViewFromObject(view: View, obj: Object): Boolean = {
    view.equals(obj);
  }

  private def setImageUrl(url: String, imageView: ImageView) {
    Log.i(TAG, url)
    ImageCacheManager.imageLoader.foreach {
      _.get(url, new ImageListenerJava {
        def onResponse(response: ImageContainerJava, isImmediate: Boolean) {
          setImageBitmap(imageView, response.getBitmap, actionBarSize, statusBarSize)
        }

        def onErrorResponse(e: VolleyError) {
          //error(e)
          Log.w(TAG, e)
        }
      })
    }
  }

  private def setImageBitmap(imageView: ImageView, bitmap: Bitmap, actionBarSize: Int, statusBarHeight: Int) {
    if (bitmap == null) {
      return
    }
    val srcWidth = bitmap.getWidth
    val srcHeight = bitmap.getHeight

    val (screenWidth, screenHeight) = screenSize

    val drawWidth = screenWidth.toFloat
    val drawHeight = (screenHeight - actionBarSize - statusBarHeight).toFloat

    val widthScale = drawWidth / srcWidth
    val heightScale = drawHeight / srcHeight

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
    imageView.setImageBitmap(resizedBitmap)

    val resizeAfterMatrix = imageView.getImageMatrix

    resizeAfterMatrix.getValues(values)
    values(Matrix.MTRANS_X) = (drawWidth - resizedBitmap.getWidth) / 2
    resizeAfterMatrix.setValues(values)

    imageView.setImageMatrix(resizeAfterMatrix)
  }
}
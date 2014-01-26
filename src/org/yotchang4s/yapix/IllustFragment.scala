package org.yotchang4s.yapix

import java.text.SimpleDateFormat

import scala.concurrent.ExecutionContext.Implicits.global

import android.graphics._
import android.os.Bundle
import android.util.Log
import android.view._
import android.widget._
import android.content.Intent
import android.webkit.WebView

import com.android.volley.VolleyError

import org.yotchang4s.scala.FutureUtil._
import org.yotchang4s.android._
import org.yotchang4s.android.Listeners._
import org.yotchang4s.android.ToastMaster
import org.yotchang4s.android.UIExecutionContext

import org.yotchang4s.pixiv.illust._
import org.yotchang4s.pixiv.PixivException
import org.yotchang4s.pixiv.PixivException.IOError

import org.yotchang4s.yapix.volley.ImageListenerJava
import org.yotchang4s.yapix.volley.ImageContainerJava

import org.yotchang4s.yapix.manga.MangaActivity
import org.yotchang4s.yapix.YapixConfig.yapixConfig

class IllustFragment extends AbstractFragment { self =>
  private val TAG = getClass.getSimpleName

  private[this] val postDateTimeFormat = new SimpleDateFormat("yyyy/MM/dd kk:mm")

  private[this] var illust: Illust = null

  private[this] var netWorkImageView: ImageView = null
  private[this] var illustDetailProgressBar: ProgressBar = null
  private[this] var illustDetailView: View = null

  private[this] var isGetMiddleImage = false
  private[this] var viewGroup: ViewGroup = null

  private[this] var childFragment: AbstractFragment = null

  protected override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)

    illust = getArguments.get(ArgumentKeys.Illust).asInstanceOf[Illust]
  }

  override protected def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    viewGroup = inflater.inflate(R.layout.illust_fragment, container, false).asInstanceOf[ViewGroup]

    netWorkImageView = viewGroup.findViewById(R.id.illust).asInstanceOf[ImageView]
    illustDetailProgressBar = viewGroup.findViewById(R.id.illustDetailProgressBar).asInstanceOf[ProgressBar]
    illustDetailView = viewGroup.findViewById(R.id.illustDetail)

    netWorkImageView.onClicks += { _ =>
      illust match {
        case d: IllustDetail if (d.manga) =>
          val tran = getChildFragmentManager.beginTransaction

          val intent = new Intent
          val clazz = classOf[MangaActivity]
          intent.setClass(getActivity.getApplicationContext, clazz)
          intent.putExtra(ArgumentKeys.IllustDetail, illust)

          Log.i(TAG, "start activity :" + clazz.getName)

          startActivity(intent)

        case d: IllustDetail =>
        case i =>
      }
    }
    viewGroup
  }

  override protected def onActivityCreated(savedInstanceState: Bundle) {
    super.onActivityCreated(savedInstanceState)

    setImageUrl(illust.thumbnailImageUrl)
    asyncSetIllustDetail(illust)
  }

  private def findViewById[T](id: Int) = {
    viewGroup.findViewById(id).asInstanceOf[T]
  }

  private def asyncSetIllustDetail(illust: Illust) {
    findViewById[TextView](R.id.illustTitle).setText(illust.title)

    illustDetailProgressBar.setVisibility(View.VISIBLE)

    val (future, cancel) = cancellableFuture[Either[PixivException, IllustDetail]](future => {
      illust.detail
    })

    future.onSuccess {
      case Right(d) =>
        this.illust = d
        illustDetailProgressBar.setVisibility(View.GONE)

        if (!d.caption.isEmpty) {
          findViewById[WebView](R.id.illustCaption)
            .loadData(d.caption, "text/html; charset=UTF-8", "UTF8")
        } else {
          findViewById[View](R.id.illustCaptionContainer).setVisibility(View.GONE)
        }
        findViewById[TextView](R.id.illustAuthor).setText(d.user.name)
        findViewById[TextView](R.id.illustPostedDateTime)
          .setText(postDateTimeFormat.format(d.postedDateTime))

        val tagContainer = findViewById[ViewGroup](R.id.illustTags)
        d.tags.foreach { t =>
          val tagTextView = new TextView(getActivity, null, R.attr.illustTagStyle)
          tagTextView.setText(t)
          tagContainer.addView(tagTextView)
        }

        findViewById[TextView](R.id.illustViewCount).setText(d.viewCount.toString)
        findViewById[TextView](R.id.illustEvaluationCount).setText(d.evaluationCount.toString)
        findViewById[TextView](R.id.illustEvaluation).setText(d.evaluation.toString)

        illustDetailView.setVisibility(View.VISIBLE)
        setImageUrl(d.middleImageUrl)
      case Left(e) =>
        illustDetailProgressBar.setVisibility(View.GONE)
        error(TAG, e)

    }(new UIExecutionContext)
  }

  private def setImageBitmap(bitmap: Bitmap) {
    if (bitmap == null) {
      return
    }
    val srcWidth = bitmap.getWidth
    val srcHeight = bitmap.getHeight

    val (screenWidth, screenHeight) = getScreenSize(getActivity)

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

    netWorkImageView.setImageMatrix(resizeAfterMatrix)
  }

  private def setImageUrl(url: String) {
    Log.i(TAG, url)
    ImageCacheManager.imageLoader.foreach {
      _.get(url, new ImageListenerJava {
        def onResponse(response: ImageContainerJava, isImmediate: Boolean) {
          setImageBitmap(response.getBitmap())
        }

        def onErrorResponse(e: VolleyError) {
          error(TAG, new PixivException(IOError))
        }
      })
    }
  }
}
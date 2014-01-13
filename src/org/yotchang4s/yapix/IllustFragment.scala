package org.yotchang4s.yapix

import android.os.Bundle
import org.yotchang4s.pixiv.illust._
import android.view._
import com.android.volley.toolbox.NetworkImageView
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

class IllustFragment extends Fragment {
  private[this] val TAG = getClass.getSimpleName

  private var illust: Illust = null

  private var netWorkImageView: NetworkImageView = null

  protected override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)

    illust = getArguments.get(ArgumentKeys.Illust).asInstanceOf[Illust]
  }

  override protected def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val viewGroup = inflater.inflate(R.layout.illust_fragment, container, false)

    netWorkImageView = viewGroup.findViewById(R.id.illust).asInstanceOf[NetworkImageView]

    ImageCacheManager.imageLoader.foreach(netWorkImageView.setImageUrl(illust.thumbnailImageUrl, _))

    illust match {
      case d: IllustDetail =>
        ImageCacheManager.imageLoader.foreach(netWorkImageView.setImageUrl(d.middleImageUrl, _))
      case i =>
        getDetail(illust, YapixConfig.yapixConfig)
    }

    ImageCacheManager.imageLoader.foreach { l =>
      netWorkImageView.setImageUrl(illust.thumbnailImageUrl, l)
      getDetail(illust, YapixConfig.yapixConfig)
    }

    viewGroup
  }

  private def getDetail(illust: Illust, config: Config) {
    import scala.concurrent.ExecutionContext.Implicits.global
    import org.yotchang4s.scala.FutureUtil._
    val (future, cancel) = cancellableFuture[Either[PixivException, IllustDetail]](future => {
      illust.detail(config)
    })

    future.onSuccess {
      case Right(d) =>
        ImageCacheManager.imageLoader.foreach {
          _.get(d.middleImageUrl, new ImageListenerJava {
            def onResponse(response: ImageContainerJava, isImmediate: Boolean) {
              netWorkImageView.setImageBitmap(response.getBitmap)
            }

            def onErrorResponse(e: VolleyError) {
              error(e)
            }
          })
        }
      case Left(e) =>
        error(e)

    }(new UIExecutionContext())
  }

  private def error(e: Exception) {
    val message = if (e.getMessage != null) "\n" + e.getMessage else ""
    ToastMaster.makeText(getActivity, "接続に失敗しました" + message, Toast.LENGTH_LONG).show
    Log.w("接続失敗", e)
  }
}
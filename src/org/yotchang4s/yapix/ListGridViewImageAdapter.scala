package org.yotchang4s.yapix

import scala.concurrent.Future
import android.app.ActivityManager
import android.content.Context
import android.graphics._
import android.util.LruCache
import android.widget._
import android.view._
import org.yotchang4s.android._
import org.yotchang4s.pixiv.http._
import org.yotchang4s.pixiv.illust._
import org.yotchang4s.pixiv.PixivException
import org.yotchang4s.pixiv.ranking.RankingIllust
import org.yotchang4s.yapix.YapixConfig._
import org.yotchang4s.pixiv.PixivException
import java.io.File
import com.android.volley.toolbox.NetworkImageView
import com.android.volley.toolbox.ImageLoader

class ListGridViewImageAdapter[T <: Illust](
  gridView: ObservableGridView,
  imageDip: Int,
  paddingDip: Int)
  extends GridViewSquareDipAdapter[NetworkImageView](gridView, imageDip, paddingDip) {

  private var dip: Int = 0
  private var list: List[T] = Nil

  override def getView(position: Int, convertView: View, parent: ViewGroup): View = {
    val view = super.getView(position, convertView, parent).asInstanceOf[NetworkImageView]

    ImageCacheManager.imageLoader.foreach { l =>
      view.setImageUrl(this.list(position).url, l)
      view.setBackgroundColor(Color.rgb(0x00, 0x66, 0x99))
    }

    /*
    val illust = this.list(position)
    val image = imageCache.get(illust.identity.value)
    if (image != null) {
      view.setImageBitmap(image)
    } else {
      view.setImageBitmap(null)
    }

    if (futures.contains(illust.identity)) {
      return view
    }
    futures += illust.identity

    import scala.concurrent._
    import ExecutionContext.Implicits.global
    val f = future {

      val http = new Http
      http.userAgent("Yapix")

      import org.yotchang4s.util.Loan
      import org.yotchang4s.util.Loan._

      try {
        for (in <- Loan(http.get(illust.url).asStream)) {
          Right(BitmapFactory.decodeStream(in))
        }
      } catch {
        case e: Exception => Left(new PixivException(e))
      }
    }

    f.onSuccess {
      case Right(i) =>
        imageCache.put(illust.identity.value, i)
        notifyDataSetChanged
        gridView.invalidate
        futures = futures - illust.identity
      case Left(e) =>
        Toast.makeText(gridView.getContext, "イラストの取得に失敗しました", Toast.LENGTH_LONG).show
        futures = futures - illust.identity
    }(new UIExecutionContext)
*/
    view
  }

  def createView(position: Int, convertView: View, parent: ViewGroup): NetworkImageView = {
    val tv = new NetworkImageView(gridView.getContext)

    tv.setBackgroundColor(Color.rgb(0, 153, 204))
    tv
  }

  def setList(list: List[T]) {
    this.list = if (list != null) list else Nil
    gridView.computeScrollY
    notifyDataSetChanged
  }

  def getList: List[T] = list

  def getItem(position: Int) = list(position)

  def getCount = list.size

  def getItemId(position: Int) = position.hashCode * 31 + list(position).identity.hashCode * 31
}
package org.yotchang4s.yapix

import android.os.Bundle
import org.yotchang4s.pixiv.illust._
import android.view._
import com.android.volley.toolbox.NetworkImageView
import android.support.v4.app.Fragment

class IllustFragment extends Fragment {

  private var illust: Illust = null

  protected override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)

    illust = getArguments.get(ArgumentKeys.Illust).asInstanceOf[Illust]
  }

  override protected def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val viewGroup = inflater.inflate(R.layout.illust_fragment, container, false)

    val view = viewGroup.findViewById(R.id.illust).asInstanceOf[NetworkImageView]

    ImageCacheManager.imageLoader.foreach { l =>
      view.setImageUrl(illust.url, l)
    }

    viewGroup
  }

}
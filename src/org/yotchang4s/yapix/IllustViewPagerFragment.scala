package org.yotchang4s.yapix

import android.view._
import android.os.Bundle
import com.android.volley.toolbox.NetworkImageView
import android.support.v4.view.ViewPager
import android.support.v4.app.Fragment
import org.yotchang4s.pixiv.illust.Illust
import android.view.View.OnKeyListener

class IllustViewPagerFragment extends Fragment {
  private var viewPager: ViewPager = null
  private var adapter: IllustPagerAdapter = null

  setRetainInstance(true)

  override protected def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val viewGroup = inflater.inflate(R.layout.illust_pager_fragment, container, false)

    viewPager = viewGroup.findViewById(R.id.illustPager).asInstanceOf[ViewPager]

    viewGroup
  }

  protected override def onActivityCreated(savedInstanceState: Bundle) {
    super.onActivityCreated(savedInstanceState)

    val adapter = new IllustPagerAdapter(getFragmentManager)

    val illustList = {
      val serializableArray = getArguments.get(ArgumentKeys.IllustList).asInstanceOf[Array[_]]
      serializableArray.map(_.asInstanceOf[Illust]).toList
    }
    val illustPosition = getArguments.getInt(ArgumentKeys.IllustListPosition)

    adapter.setList(illustList)
    viewPager.setAdapter(adapter)
    viewPager.setCurrentItem(illustPosition)
  }
}
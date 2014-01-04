package org.yotchang4s.yapix

import android.view._
import android.os.Bundle
import com.android.volley.toolbox.NetworkImageView
import android.support.v4.view.ViewPager
import android.support.v4.app.Fragment
import org.yotchang4s.pixiv.illust.Illust

class IllustViewPagerFragment extends Fragment {
  private var viewPager: ViewPager = null
  private var adapter: IllustPagerAdapter = null

  override protected def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val viewGroup = inflater.inflate(R.layout.illust_pager_fragment, container, false)

    viewPager = viewGroup.findViewById(R.id.illustPager).asInstanceOf[ViewPager]

    viewGroup
  }

  protected override def onActivityCreated(savedInstanceState: Bundle) {
    super.onActivityCreated(savedInstanceState)
    
    val adapter = new IllustPagerAdapter(getFragmentManager)

    val illustList = getArguments.get(ArgumentKeys.IllustList).asInstanceOf[Array[Illust]].toList
    val illustPosition = getArguments.getInt(ArgumentKeys.IllustListPosition)
    import org.yotchang4s.scala.WrapAsScala._

    adapter.setList(illustList)
    viewPager.setAdapter(adapter)
    viewPager.setCurrentItem(illustPosition)
  }
}
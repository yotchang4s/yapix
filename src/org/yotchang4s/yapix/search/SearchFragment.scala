package org.yotchang4s.yapix.search

import android.os.Bundle
import android.view._
import org.yotchang4s.yapix.AbstractFragment
import org.yotchang4s.yapix.R
import android.widget._
import org.yotchang4s.android.Listeners._
import org.yotchang4s.yapix._

object SearchFragment {
  sealed trait SearchType
  case object Tag extends SearchType
}

class SearchFragment extends AbstractFragment {
  private val TAG = getClass.getSimpleName

  override protected def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val viewGroup = inflater.inflate(R.layout.search, container, false).asInstanceOf[ViewGroup]

    val searchEditText = viewGroup.findViewById(R.id.searchEditText).asInstanceOf[EditText]

    val tagButton = viewGroup.findViewById(R.id.searchTag).asInstanceOf[Button]

    tagButton.onClicks += { v =>
      val tran = getChildFragmentManager.beginTransaction

      val searchResultFragment = new SearchResultFragment
      childFragment(searchResultFragment)

      val bundle = new Bundle
      bundle.putSerializable(ArgumentKeys.SearchType, SearchFragment.Tag)
      bundle.putString(ArgumentKeys.SearchKeyword, searchEditText.getText.toString)

      searchResultFragment.setArguments(bundle)

      tran.add(R.id.searchContent, searchResultFragment)
      tran.addToBackStack(null)
      tran.commit
    }
    viewGroup
  }

  override protected def onActivityCreated(savedInstanceState: Bundle) {
    super.onActivityCreated(savedInstanceState)
  }
}
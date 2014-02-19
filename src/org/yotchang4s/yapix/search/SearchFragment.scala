package org.yotchang4s.yapix.search

import android.os.Bundle
import android.view._
import org.yotchang4s.yapix.AbstractFragment
import org.yotchang4s.yapix.R
import android.widget._
import org.yotchang4s.android.Listeners._
import org.yotchang4s.yapix._

class SearchFragment extends AbstractFragment {

  override protected def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val viewGroup = inflater.inflate(R.layout.search, container, false).asInstanceOf[ViewGroup]

    val searchEditText = viewGroup.findViewById(R.id.searchEditText).asInstanceOf[EditText]

    val tagButton = viewGroup.findViewById(R.id.searchTag).asInstanceOf[Button]
    val captionButton = viewGroup.findViewById(R.id.searchCaption).asInstanceOf[Button]

    tagButton.onClicks += { _ => screenTransition(Search.Tag) }
    captionButton.onClicks += { _ => screenTransition(Search.Caption) }

    def screenTransition(searchType: Search.Type) {
      val tran = getChildFragmentManager.beginTransaction

      val searchResultFragment = new SearchResultFragment
      childFragment(searchResultFragment)

      val bundle = new Bundle
      bundle.putSerializable(ArgumentKeys.SearchType, searchType)
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
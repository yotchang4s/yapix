package org.yotchang4s.yapix

import android.support.v4.app.Fragment

abstract class AbstractFragment extends Fragment {
  def onBackPressed: Boolean = false
}
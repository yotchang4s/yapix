package org.yotchang4s.yapix

import android.content.Context
import org.yotchang4s.pixiv.illust.Illust

class RankingGridAdapter(context: Context,
  imageDip: Int,
  paddingDip: Int) extends ListGridViewImageAdapter[Illust](context, imageDip, paddingDip)
package org.yotchang4s.pixiv.manga

import org.yotchang4s.pixiv.illust.Illust

trait Manga extends Illust {
  def manga: Boolean
}
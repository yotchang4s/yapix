package org.yotchang4s.pixiv.bookmark

import org.yotchang4s.pixiv.PixivException

trait Paging[T] {
  val list: List[T]

  def next: Either[PixivException, Option[Paging[T]]]
  def prev: Either[PixivException, Option[Paging[T]]]
}
package org.yotchang4s.pixiv.novel

trait TagComponent {
  val tag: TagRepository

  trait TagRepository {
  }
}
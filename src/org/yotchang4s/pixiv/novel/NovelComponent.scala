package org.yotchang4s.pixiv.novel

trait NovelComponent {
  val novel: NovelRepository

  trait NovelRepository {
  }
}
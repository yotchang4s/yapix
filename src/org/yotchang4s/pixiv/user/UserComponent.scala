package org.yotchang4s.pixiv.novel

trait UserComponent {
  val user: UserRepository

  trait UserRepository {
  }
}
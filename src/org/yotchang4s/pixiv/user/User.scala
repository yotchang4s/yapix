package org.yotchang4s.pixiv.user

import org.yotchang4s.pixiv.Identity
import org.yotchang4s.pixiv.Entity

case class UserId(value: String) extends Identity[String]

class User(
  val identity: UserId,
  val name: String,
  val profileImageUrl: String) extends Entity[UserId] {
}
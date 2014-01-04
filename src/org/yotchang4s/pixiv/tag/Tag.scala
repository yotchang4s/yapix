package org.yotchang4s.pixiv.tag

import org.yotchang4s.pixiv.Entity
import org.yotchang4s.pixiv.Identity
import org.yotchang4s.pixiv.Identity

case class TagId(value: String) extends Identity[String]

class Tag(val identity: TagId) extends Entity[TagId]
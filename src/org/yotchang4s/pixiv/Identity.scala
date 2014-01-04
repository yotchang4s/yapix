package org.yotchang4s.pixiv

@serializable
trait Identity[+A] extends Serializable {
  val value: A
}
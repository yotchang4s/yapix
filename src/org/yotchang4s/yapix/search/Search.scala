package org.yotchang4s.yapix.search

object Search {
  sealed trait Type extends Serializable
  case object Tag extends Type
  case object Caption extends Type
}
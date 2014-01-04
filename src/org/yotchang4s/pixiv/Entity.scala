package org.yotchang4s.pixiv

trait Entity[+I] extends Serializable {
  val identity: I

  override def equals(other: Any): Boolean = {
    other match {
      case o: Entity[I] => o.identity.equals(identity)
      case _ => false
    }
  }

  override def hashCode: Int = 31 * identity.##
}
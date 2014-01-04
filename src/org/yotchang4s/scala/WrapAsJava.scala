package org.yotchang4s.scala

class WrapAsJava extends scala.collection.convert.WrapAsJava {
  def asArrayList[T](list: List[T]): java.util.ArrayList[T] = {
    val arrayList = new java.util.ArrayList[T]
    (0 to list.size - 1) foreach (i => arrayList.add(list(i)))

    arrayList
  }
}

object WrapAsJava extends WrapAsJava
package org.yotchang4s.scala

class WrapAsScala extends scala.collection.convert.WrapAsScala {
   def asList[T](list: java.util.List[T]): List[T] = {
    list.toList
  }
}

object WrapAsScala extends WrapAsScala

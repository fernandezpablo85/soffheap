package com.despegar.soffheap.util

trait Managed {
  def close():Unit
}

object Managed {
  implicit def close2Managed[A <: {def close():Any}](c:A):Managed = Managed(c.close())

  def apply(closeResource: => Unit) = new Managed {
    def close(): Unit = closeResource
  }

  def managed[A <% Managed](res: => A) = new Traversable[A] {
    def foreach[U](f: (A) => U) {
      val closeable:A = res
      try {
        f(closeable)
      }finally{
        closeable.close()
      }
    }
  }
}
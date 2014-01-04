package org.yotchang4s.android

import concurrent.ExecutionContext
import android.app.Activity
import android.os.Handler

class UIExecutionContext extends ExecutionContext {
  private val handler = new Handler
  
  def execute(runnable: Runnable): Unit = handler.post(runnable)

  def reportFailure(t: Throwable): Unit = t match {
    case e: Exception => e.printStackTrace
  }
}
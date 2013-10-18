package com.despegar.soffheap.concurrent;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * ThreadFactory implementation that creates daemon threads.
 * 
 * @author  Paul Ferraro
 * @since   1.1
 */
public class DaemonThreadFactory implements ThreadFactory
{
  private static ThreadFactory instance = new DaemonThreadFactory();
  
  private ThreadFactory factory = Executors.defaultThreadFactory();
  
  /**
   * Returns single shared instance
   * @return a ThreadFactory instance
   */
  public static ThreadFactory getInstance()
  {
    return instance;
  }
  
  /**
   * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
   */
  @Override
  public Thread newThread(Runnable runnable)
  {
    Thread thread = this.factory.newThread(runnable);
    
    thread.setDaemon(true);

    return thread;
  }
}
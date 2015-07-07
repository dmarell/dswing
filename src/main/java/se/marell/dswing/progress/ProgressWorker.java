/*
 * Copyright (c) 2010,2011 Daniel Marell
 * All rights reserved.
 *
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 *
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package se.marell.dswing.progress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.marell.dcommons.progress.ProgressTracker;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class provides support for threading a task with a progress feedback.<p>
 * 
 * Usage example:
 * 
 * <pre><code>
 * ProgressWorker t = new ProgressWorker() {
 *   {@literal @}Override
 *   protected void runTask() {
 *    // lengthy work here.
 *    // call setProgressValue(x) to set progress
 *    // check for cancel by calling isCancelled()
 *   }
 * 
 *   {@literal @}Override
 *   protected void notifyTaskEnded(boolean cancelled) { ... }
 * };
 * 
 * t.start();
 * </code></pre>
 */
public abstract class ProgressWorker {
  private Logger logger = LoggerFactory.getLogger(ProgressWorker.class);
  private static final int UPDATE_PROGRESS_INTERVAL = 100;
  private boolean isRunning;
  private boolean isCancelled;
  private float currentValue;
  private String currentText;
  private float lastValue;
  private String lastText;
  private Timer updateProgressTimer;

  /**
   * Starts the task. Only call this once for a ProgressWorker object.
   */
  public void start() {
    isRunning = true;
    SwingWorker worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
        try {
          runTask();
          return this;
        } catch (Throwable t) {
          setProgressText("Unexpected exception: " + t.getMessage());
          logger.error("Unexpected exception when executing progress task: ", t);
          return null;
        }
      }

      @Override
      protected void done() {
        updateProgressTimer.stop();
        isRunning = false;
        setProgressValue(0);
        updateProgress();
        notifyTaskEnded(isCancelled() || isCancelled);
      }
    };

    updateProgressTimer = new Timer(UPDATE_PROGRESS_INTERVAL, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent evt) {
        updateProgress();
      }
    });
    updateProgressTimer.start();

    worker.execute();
  }

  public void cancelTask() {
    isCancelled = true;
  }

  protected final boolean isCancelled() {
    return isCancelled;
  }

  private void updateProgress() {
    float value;
    String text;
    synchronized (this) {
      value = currentValue;
      text = currentText;
    }
    if (value != lastValue) {
      notifyProgressValueChanged(value);
      lastValue = value;
    }
    if (lastText == null || !text.equals(lastText)) {
      notifyProgressTextChanged(text);
      lastText = text;
    }
  }

  /**
   * The lengthy task. This method executes in a separate
   * thread - don't call any swing components from it.
   */
  protected abstract void runTask();

  protected abstract void notifyProgressValueChanged(float value);

  protected abstract void notifyProgressTextChanged(String text);

  /**
   * Called when lengthy task is finished or interrupted. This method is called from the swing thread.
   *
   * @param cancelled true if task was cancelled
   */
  protected abstract void notifyTaskEnded(boolean cancelled);

  public final boolean isRunning() {
    return isRunning;
  }

  protected final void setProgress(float value, String text) {
    synchronized (this) {
      currentValue = value;
      currentText = text;
    }
  }

  protected final void setProgressValue(float value) {
    synchronized (this) {
      currentValue = value;
    }
  }

  protected final void setProgressText(String text) {
    synchronized (this) {
      currentText = text;
    }
  }

  protected ProgressTracker createProgressTracker() {
    return new ProgressTracker() {
      @Override
      public boolean isCancelled() {
        return ProgressWorker.this.isCancelled();
      }

      @Override
      public void setTotalProgress(float value) {
        setProgressValue(value);
      }

      @Override
      public void setProgressLabel(String text) {
        setProgressText(text);
      }

      @Override
      public void activityReport(int count) {
      }
    };
  }
}

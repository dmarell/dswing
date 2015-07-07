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
import se.marell.dcommons.time.PassiveTimer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class provides support for threading a task with a progress bar in a popup dialog.<p>
 * The class allows for customizing the dialog content and the progress retrieval.<p>
 * 
 * Usage example:
 * 
 * <pre>
 * ProgressWorkerPopup t = new ProgressWorkerPopup(dialog, "Initializing...", 200, true, 2000, true, true) {
 *   {@literal @}Override
 *   protected void runTask() {
 *    // lengthy work here.
 *    // call setProgressValue(x) to set progress
 *    // check for cancel by calling isInterrupted()
 *   }
 *
 *   {@literal @}Override
 *   protected void notifyTaskEnded(boolean cancelled) { ... }
 * };
 *
 * t.start(); // Start the task
 * </pre>
 */
public abstract class ProgressWorkerPopup {
    private static final int PROGRESS_DIALOG_STEPS = 100;

    private Logger logger = LoggerFactory.getLogger(ProgressWorkerPopup.class);
    private Window owner;
    private String title;
    private int progressUpdatePeriod;
    private boolean interrupted;
    private boolean disableOwner;
    private int dialogDelay;
    private boolean useCancelButton;

    private float progressValue;
    private Timer timer;
    private JDialog progressDialog;
    private JProgressBar progressBar;
    private PassiveTimer delayTimer;
    private boolean useProgressDialog;
    private boolean isRunning;

    /**
     * @param owner                Owning window
     * @param title                Title of progress bar window
     * @param progressUpdatePeriod msec between updates
     * @param disableOwner         If true, disables owning window while executing
     * @param dialogDelay          Delay in msec before the progress dialog pops up
     * @param useCancelButton      If true, the progress dialog is equipped with a cancel button
     * @param useProgressDialog    If true, a progress dialog will be used
     */
    protected ProgressWorkerPopup(Window owner, String title,
                                  int progressUpdatePeriod, boolean disableOwner,
                                  int dialogDelay, boolean useCancelButton, boolean useProgressDialog) {
        this.owner = owner;
        this.title = title;
        this.progressUpdatePeriod = progressUpdatePeriod;
        this.disableOwner = disableOwner;
        this.dialogDelay = dialogDelay;
        this.useCancelButton = useCancelButton;
        this.useProgressDialog = useProgressDialog || useCancelButton;
    }

    /**
     * Starts the task. Only call this once for a ProgressWorkerPopup object.
     */
    public final void start() {
        isRunning = true;
        interrupted = false;
        if (owner != null && disableOwner) {
            owner.setEnabled(false);
        }

        final SwingWorker worker = new SwingWorker() {
            @Override
            public Object doInBackground() {
                try {
                    runTask();
                    return this;
                } catch (Throwable t) {
                    setProgressText("Unexpected exception: " + t.getMessage());
                    logger.error("Unexpected exception when executing progress task: " + title, t);
                    return null;
                }
            }

            @Override
            public void done() {
                isRunning = false;
                if (progressDialog != null) {
                    progressDialog.dispose();
                }

                if (owner != null && disableOwner) {
                    owner.setEnabled(true);
                    owner.requestFocus();
                }

                timer.stop();
                notifyTaskEnded(interrupted);
            }
        };

        delayTimer = new PassiveTimer(dialogDelay);
        timer = new Timer(progressUpdatePeriod, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                updateProgressDialog();
            }
        });

        updateProgressDialog();
        timer.start();
        worker.execute();
    }

    protected void updateProgressDialog() {
        if (useProgressDialog) {
            if (progressDialog == null && delayTimer.hasExpired()) {
                createProgressDialog();
            }
        }

        if (progressBar != null) {
            progressBar.setValue((int) (getProgressValue() * PROGRESS_DIALOG_STEPS));
        }
    }

    private void createProgressDialog() {
        progressDialog = new JDialog(owner, title);

        createProgressBar();

        progressDialog.getContentPane().add(createDialogPanel());
        progressDialog.setMinimumSize(getProgressDialogMinimumSize());
        progressDialog.pack();

        if (owner != null) {
            progressDialog.setLocationRelativeTo(owner);
        }

        progressDialog.setVisible(true);
    }

    protected Dimension getProgressDialogMinimumSize() {
        return new Dimension(200, 50);
    }

    /**
     * Create the progress bar object. Sub classes can override to return a customized version.
     * Call @see #setProgressBar(JProgressBar) before returning if a progress bar should be used.
     */
    protected void createProgressBar() {
        JProgressBar pbar = new JProgressBar(0, PROGRESS_DIALOG_STEPS);
        pbar.setValue(0);
        pbar.setStringPainted(true);
        setProgressBar(pbar);
    }

    /**
     * Sub classes override this to provide their own progress dialog content.
     * Default it contains the progress bar and a cancel button (if parameter useCancelButton is true).
     *
     * @return Panel
     */
    protected JPanel createDialogPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(5, 5, 5, 5));
        p.add(getProgressBar(), BorderLayout.NORTH);

        if (useCancelButton) {
            final JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    interruptTask();
                    cancelButton.setEnabled(false);
                }
            });
            JPanel p2 = new JPanel();
            p2.add(cancelButton);
            p.add(p2, BorderLayout.SOUTH);
        }
        return p;
    }

    /**
     * The lengthy task. Sub classes must override. This method executes in a separate
     * thread - don't call any swing components from it
     */
    protected abstract void runTask();

    /**
     * Called when lengthy task is finished or interrupted. This method is called from the swing thread.
     *
     * @param cancelled True is task was cancelled
     */
    protected abstract void notifyTaskEnded(boolean cancelled);

    /**
     * Return the current progress value - a value counting from 0 to 1.
     * Override this in sub classes if you don't want to use the @see #setProgressValue(float) method
     * from the lengthy task. This method is called from the swing thread.
     *
     * @return Progress value between 0.0 and 1.0
     */
    protected float getProgressValue() {
        return progressValue;
    }

    /**
     * Set current progress to an absolute value between 0 and 1. Used by lengthy task thread
     * to report progress unless @see getProgressValue() is overridden.
     *
     * @param value Progress value between 0.0 and 1.0
     */
    public void setProgressValue(float value) {
        progressValue = value;
    }

    /**
     * Set progress text.
     *
     * @param text A text describing the current step in the running task
     */
    protected void setProgressText(String text) {
    }

    /**
     * Set the progress bar to use.
     *
     * @param p Progress bar
     */
    protected final void setProgressBar(JProgressBar p) {
        progressBar = p;
    }

    /**
     * Get the progress bar, for use by subclasses when composing their own progress dialogs
     *
     * @return Progress bar object
     */
    protected final JProgressBar getProgressBar() {
        return progressBar;
    }

    /**
     * Check if lengthy task were interrupted. This method should be checked by the lengthy task implementation
     *
     * @return true is task were interrupted
     */
    public final boolean isInterrupted() { // todo is named isCancelled in ProgressWorker. Align
        return interrupted;
    }

    /**
     * Interrupt the lengthy task. The lengthy task detects this by calling @see #isInterrupted()
     * and returns as soon as it can. The Cancel button uses this method.
     */
    public final void interruptTask() {
        interrupted = true;
    }

    /**
     * Enables sub classes to set the visibility of the progress dialog there is one.
     *
     * @param visible true if dialog should be visible
     */
    protected final void setProgressDialogVisible(boolean visible) {
        if (progressDialog != null) {
            progressDialog.setVisible(visible);
        }
    }

    protected Window getWindowOwner() {
        return owner;
    }

    /**
     * Check if task is running.
     *
     * @return true if task is running
     */
    public boolean isRunning() {
        return isRunning;
    }

    protected ProgressTracker createProgressTracker() {
        return new ProgressTracker() {
            @Override
            public boolean isCancelled() {
                return ProgressWorkerPopup.this.isInterrupted();
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

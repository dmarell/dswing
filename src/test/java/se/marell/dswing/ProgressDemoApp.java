/*
 * Created by Daniel Marell 12-07-03 11:28 PM
 */
package se.marell.dswing;

import se.marell.dcommons.progress.EmptyProgressTracker;
import se.marell.dcommons.progress.PrintlnProgressTracker;
import se.marell.dcommons.progress.ProgressTracker;
import se.marell.dswing.progress.ProgressWorkerPopup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ProgressDemoApp {

    public static void main(String[] args) {
        final LengthyTask task = new LengthyTask();
        final JFrame frame = new JFrame();

        JButton buttonRunEmpty = new JButton("Run EmptyProgressTracker");
        buttonRunEmpty.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        task.run(new EmptyProgressTracker(), 20);
                    }
                });
                t.start();
            }
        });

        JButton buttonRunPrintln = new JButton("Run PrintlnProgressTracker");
        buttonRunPrintln.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        task.run(new PrintlnProgressTracker(), 20);
                    }
                });
                t.start();
            }
        });

        JButton buttonRunProgressbar = new JButton("Run ProgressbarProgressTracker");
        buttonRunProgressbar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                final ProgressWorkerPopup progressWorker = new ProgressWorkerPopup(frame, "Executing", 200, true, 200, true, true) {
                    @Override
                    protected void runTask() {
                        task.run(createProgressTracker(), 20);
                    }

                    @Override
                    protected void notifyTaskEnded(boolean cancelled) {
                    }
                };
                progressWorker.start();
            }
        });

        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = GridBagConstraints.PAGE_END;
        p.add(buttonRunEmpty, gbc);
        p.add(buttonRunPrintln, gbc);
        p.add(buttonRunProgressbar, gbc);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(p);
        frame.pack();
        frame.setVisible(true);
    }
}

class LengthyTask {
    public void run(final ProgressTracker pt, final int length) {
        for (int i = 0; i < length; ++i) {
            // Simulate lengthy work
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }

            // Update progress and check is cancelled
            if (pt.isCancelled()) {
                return;
            }
            pt.setProgressLabel("Executing step " + i + " of " + length);
            pt.setTotalProgress(i / (float) length);
        }
    }
}
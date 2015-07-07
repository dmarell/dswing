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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Simple component displaying a rotating circle segment. Used to indicate some action where a progress bar
 * is not suitable.
 *
 * Consider using an animated image instead of this component.
 */
public class ActionSpinner extends JComponent {
    private int msecIdleBeforeInvisible;
    private long lastActionTimestamp;
    private int counter;
    private Timer timer;

    /**
     * Construct an ActionSpinner component.
     *
     * @param msecIdleBeforeInvisible Set to 0 if the component shall be always visible. Else specify the timeout
     *                                with no action before it hides.
     */
    public ActionSpinner(int msecIdleBeforeInvisible) {
        this.msecIdleBeforeInvisible = msecIdleBeforeInvisible;
        Dimension d = new Dimension(30, 30);
        setSize(d);
        setPreferredSize(d);
    }

    public void start() {
        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tick();
            }
        });
        timer.start();
    }

    public void stop() {
        if (timer != null) {
            timer.stop();
        }
    }

    /**
     * Indicate action.
     */
    public void tick() {
        ++counter;
        lastActionTimestamp = System.currentTimeMillis();
        repaint();
    }

    @Override
    public void paint(final Graphics g) {
        if (System.currentTimeMillis() - lastActionTimestamp < msecIdleBeforeInvisible || msecIdleBeforeInvisible == 0) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(Color.GRAY);
            g.fillOval(0, 0, 30, 30);
            g.setColor(Color.BLACK);
            g.fillArc(0, 0, 30, 30, (counter % 20) * -18, -16);
        }
    }
}

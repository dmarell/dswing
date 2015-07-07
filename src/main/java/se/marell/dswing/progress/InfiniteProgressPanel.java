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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.*;

/**
 * An infinite progress panel displays a rotating figure and
 * a message to notice the user of a long, duration unknown
 * task. The shape and the text are drawn upon a white veil
 * which alpha level (or shield value) lets the underlying
 * component shine through. This panel is meant to be used
 * asa <i>glass pane</i> in the window performing the long
 * operation.
 * 
 * On the contrary to regular glass panes, you don't need to
 * set it visible or not by yourself. Once you've started the
 * animation all the mouse events are intercepted by this
 * panel, preventing them from being forwarded to the
 * underlying components.
 * 
 * The panel can be controlled by the <code>start()</code>,
 * <code>stop()</code> and <code>interrupt()</code> methods.
 * 
 * Example:
 * 
 * <pre>InfiniteProgressPanel pane = new InfiniteProgressPanel();
 * frame.setGlassPane(pane);
 * pane.start()</pre>
 * 
 * Several properties can be configured at creation time. The
 * message and its font can be changed at runtime. Changing the
 * font can be done using <code>setFont()</code> and
 * <code>setForeground()</code>.
 * 
 * This class is heavily inspired by the InfiniteProgressPanel by Romain Guy 2005.
 * However it's usefulness is limited as an animated image is a good alternative.
 */
public class InfiniteProgressPanel extends JComponent implements MouseListener, ActionListener {
    /**
     * Contains the bars composing the circular shape.
     */
    protected Area[] ticker;
    /**
     * The animation timer is responsible for fade in/out and rotation.
     */
    protected Timer animationTimer;
    /**
     * Notifies whether the animation is running or not.
     */
    protected boolean started;
    /**
     * Alpha level of the veil, used for fade in/out.
     */
    protected int alphaLevel = 0;
    /**
     * Duration of the veil's fade in/out.
     */
    protected int rampDelay = 300;
    /**
     * Alpha level of the veil.
     */
    protected float shield = 0.70f;
    /**
     * Message displayed below the circular shape.
     */
    protected String text = "";
    /**
     * Amount of bars composing the circular shape.
     */
    protected int barsCount = 14;
    /**
     * Amount of frames per seconds. Lowers this to save CPU.
     */
    protected float fps = 15.0f;
    /**
     * Rendering hints to set anti-aliasing.
     */
    protected RenderingHints hints;

    /**
     * Creates a new progress panel with default values:
     * <ul>
     * <li>No message</li>
     * <li>14 bars</li>
     * <li>Veil's alpha level is 70%</li>
     * <li>15 frames per second</li>
     * <li>Fade in/out last 300 ms</li>
     * </ul>
     */
    public InfiniteProgressPanel() {
        this("");
    }

    /**
     * Creates a new progress panel with default values:
     * <ul>
     * <li>14 bars</li>
     * <li>Veil's alpha level is 70%</li>
     * <li>15 frames per second</li>
     * <li>Fade in/out last 300 ms</li>
     * </ul>
     *
     * @param text The message to be displayed. Can be null or empty.
     */
    public InfiniteProgressPanel(String text) {
        this(text, 14);
    }

    /**
     * Creates a new progress panel with default values:
     * <ul>
     * <li>Veil's alpha level is 70%</li>
     * <li>15 frames per second</li>
     * <li>Fade in/out last 300 ms</li>
     * </ul>
     *
     * @param text      The message to be displayed. Can be null or empty.
     * @param barsCount The amount of bars composing the circular shape
     */
    public InfiniteProgressPanel(String text, int barsCount) {
        this(text, barsCount, 0.70f);
    }

    /**
     * Creates a new progress panel with default values:
     * <ul>
     * <li>15 frames per second</li>
     * <li>Fade in/out last 300 ms</li>
     * </ul>
     *
     * @param text      The message to be displayed. Can be null or empty.
     * @param barsCount The amount of bars composing the circular shape.
     * @param shield    The alpha level between 0.0 and 1.0 of the colored
     *                  shield (or veil).
     */
    public InfiniteProgressPanel(String text, int barsCount, float shield) {
        this(text, barsCount, shield, 15.0f);
    }

    /**
     * Creates a new progress panel with default values:
     * <ul>
     * <li>Fade in/out last 300 ms</li>
     * </ul>
     *
     * @param text      The message to be displayed. Can be null or empty.
     * @param barsCount The amount of bars composing the circular shape.
     * @param shield    The alpha level between 0.0 and 1.0 of the colored
     *                  shield (or veil).
     * @param fps       The number of frames per second. Lower this value to
     *                  decrease CPU usage.
     */
    public InfiniteProgressPanel(String text, int barsCount, float shield, float fps) {
        this(text, barsCount, shield, fps, 300);
    }

    /**
     * Creates a new progress panel.
     *
     * @param text      The message to be displayed. Can be null or empty.
     * @param barsCount The amount of bars composing the circular shape.
     * @param shield    The alpha level between 0.0 and 1.0 of the colored
     *                  shield (or veil).
     * @param fps       The number of frames per second. Lower this value to
     *                  decrease CPU usage.
     * @param rampDelay The duration, in milli seconds, of the fade in and
     *                  the fade out of the veil.
     */
    public InfiniteProgressPanel(String text, int barsCount, float shield, float fps, int rampDelay) {
        this.text = text;
        this.rampDelay = rampDelay >= 0 ? rampDelay : 0;
        this.shield = shield >= 0.0f ? shield : 0.0f;
        this.fps = fps > 0.0f ? fps : 15.0f;
        this.barsCount = barsCount > 0 ? barsCount : 14;

        this.hints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        this.hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        this.hints.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    }

    /**
     * Changes the displayed message at runtime.
     *
     * @param text The message to be displayed. Can be null or empty.
     */
    public void setText(String text) {
        this.text = text;
        repaint();
    }

    /**
     * Returns the current displayed message.
     *
     * @return Displayed message
     */
    public String getText() {
        return text;
    }

    /**
     * Starts the waiting animation by fading the veil in, then
     * rotating the shapes. This method handles the visibility
     * of the glass pane.
     */
    public void start() {
        addMouseListener(this);
        setVisible(true);
        ticker = buildTicker();
        if (animationTimer != null) {
            animationTimer.stop();
        }
        initTimer(true);
        animationTimer = new Timer((int) (1000f / fps), this);
        animationTimer.start();
    }

    /**
     * Stops the waiting animation by stopping the rotation
     * of the circular shape and then by fading out the veil.
     * This methods sets the panel invisible at the end.
     */
    public void stop() {
        if (animationTimer != null) {
            animationTimer.stop();
            initTimer(false);
            animationTimer = new Timer((int) (1000f / fps), this);
            animationTimer.start();
        }
    }

    /**
     * Interrupts the animation, whatever its state is. You
     * can use it when you need to stop the animation without
     * running the fade out phase.
     * This methods sets the panel invisible at the end.
     */
    public void interrupt() {
        if (animationTimer != null) {
            animationTimer.stop();
            animationTimer = null;
            removeMouseListener(this);
            setVisible(false);
        }
    }

    public void paintComponent(Graphics g) {
        if (started) {
            int width = getWidth();

            double maxY = 0.0;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHints(hints);

            g2.setColor(new Color(255, 255, 255, (int) (alphaLevel * shield)));
            g2.fillRect(0, 0, getWidth(), getHeight());

            for (int i = 0; i < ticker.length; i++) {
                int channel = 224 - 128 / (i + 1);
                g2.setColor(new Color(channel, channel, channel, alphaLevel));
                g2.fill(ticker[i]);

                Rectangle2D bounds = ticker[i].getBounds2D();
                if (bounds.getMaxY() > maxY)
                    maxY = bounds.getMaxY();
            }

            if (text != null && text.length() > 0) {
                FontRenderContext context = g2.getFontRenderContext();
                TextLayout layout = new TextLayout(text, getFont(), context);
                Rectangle2D bounds = layout.getBounds();
                g2.setColor(getForeground());
                layout.draw(g2, (float) (width - bounds.getWidth()) / 2,
                        (float) (maxY + layout.getLeading() + 2 * layout.getAscent()));
            }
        }
    }

    /**
     * Builds the circular shape and returns the result as an array of
     * <code>Area</code>. Each <code>Area</code> is one of the bars
     * composing the shape.
     *
     * @return Bar areas
     */
    private Area[] buildTicker() {
        Area[] ticker = new Area[barsCount];
        Point2D.Double center = new Point2D.Double((double) getWidth() / 2, (double) getHeight() / 2);
        double fixedAngle = 2.0 * Math.PI / ((double) barsCount);

        for (double i = 0.0; i < (double) barsCount; i++) {
            Area primitive = buildPrimitive();

            AffineTransform toCenter = AffineTransform.getTranslateInstance(center.getX(), center.getY());
            AffineTransform toBorder = AffineTransform.getTranslateInstance(45.0, -6.0);
            AffineTransform toCircle = AffineTransform.getRotateInstance(-i * fixedAngle, center.getX(), center.getY());

            AffineTransform toWheel = new AffineTransform();
            toWheel.concatenate(toCenter);
            toWheel.concatenate(toBorder);

            primitive.transform(toWheel);
            primitive.transform(toCircle);

            ticker[(int) i] = primitive;
        }

        return ticker;
    }

    /**
     * Builds a bar.
     */
    private Area buildPrimitive() {
        Rectangle2D.Double body = new Rectangle2D.Double(6, 0, 30, 12);
        Ellipse2D.Double head = new Ellipse2D.Double(0, 0, 12, 12);
        Ellipse2D.Double tail = new Ellipse2D.Double(30, 0, 12, 12);

        Area tick = new Area(body);
        tick.add(new Area(head));
        tick.add(new Area(tail));

        return tick;
    }

    private boolean rampUp;
    private boolean inRamp;
    private AffineTransform toCircle;
    private long start;

    private void initTimer(boolean rampUp) {
        this.rampUp = rampUp;

        Point2D.Double center = new Point2D.Double((double) getWidth() / 2, (double) getHeight() / 2);
        double fixedIncrement = 2.0 * Math.PI / ((double) barsCount);
        toCircle = AffineTransform.getRotateInstance(fixedIncrement, center.getX(), center.getY());

        start = System.currentTimeMillis();
        if (rampDelay == 0) {
            alphaLevel = rampUp ? 255 : 0;
        }

        started = true;
        inRamp = true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!inRamp) {
            for (Area a : ticker) {
                a.transform(toCircle);
            }
        } else {
            if (rampUp) {
                if (alphaLevel < 255) {
                    alphaLevel = (int) (255 * (System.currentTimeMillis() - start) / rampDelay);
                }
                if (alphaLevel >= 255) {
                    alphaLevel = 255;
                    inRamp = false;
                }
            } else {
                if (alphaLevel >= 0) {
                    alphaLevel = (int) (255 - (255 * (System.currentTimeMillis() - start) / rampDelay));
                }
                if (alphaLevel <= 0) {
                    alphaLevel = 0;
                    interrupt();
                    return;
                }
            }
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                repaint();
            }
        });
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
}
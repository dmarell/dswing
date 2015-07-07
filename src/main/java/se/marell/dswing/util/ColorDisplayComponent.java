/*
 * Copyright (c) 2011 Daniel Marell
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
package se.marell.dswing.util;

import javax.swing.*;
import java.awt.*;

public class ColorDisplayComponent extends JComponent {
    private Color color;

    public ColorDisplayComponent() {
        this(null, 20, 20);
    }

    public ColorDisplayComponent(Color color, int width, int height) {
        this.color = color;
        setPreferredSize(new Dimension(width, height));
    }

    public void setColor(Color color) {
        this.color = color;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = (int) getSize().getWidth();
        int h = (int) getSize().getHeight();
        if (color != null) {
            g.setColor(color);
            g.fillRect(0, 0, w - 1, h - 1);
        }
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, w - 1, h - 1);
    }
}
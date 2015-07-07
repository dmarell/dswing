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

import java.awt.*;

/**
 * Helper class for settings properties on GridBagConstraints in a compact and readable way.
 */
public class GridBagConstraintsSetter extends GridBagConstraints {
    public GridBagConstraintsSetter setXY(int gridx, int gridy) {
        this.gridx = gridx;
        this.gridy = gridy;
        return this;
    }

    public GridBagConstraintsSetter setAnchorFill(int anchor, int fill) {
        this.anchor = anchor;
        this.fill = fill;
        return this;
    }

    public GridBagConstraintsSetter setWeights(int weightx, int weighty) {
        this.weightx = weightx;
        this.weighty = weighty;
        return this;
    }

    public GridBagConstraintsSetter setWidthHeight(int gridwidth, int gridheight) {
        this.gridwidth = gridwidth;
        this.gridheight = gridheight;
        return this;
    }

    public GridBagConstraintsSetter setInsets(int top, int left, int bottom, int right) {
        this.insets.top = top;
        this.insets.left = left;
        this.insets.bottom = bottom;
        this.insets.right = right;
        return this;
    }
}

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
import java.net.URL;

/**
 * Utility methods for reading images from resources.
 */
public class ImageResources {
    /**
     * Read image from file in classpath located in a directory images below the reference class.
     *
     * @param cls      The reference class
     * @param filename Filename of image
     * @return Image or null if no such image file was found
     */
    public static Image createImage(Class<?> cls, String filename) {
        URL imgUrl = cls.getResource("images/" + filename);
        if (imgUrl != null) {
            return Toolkit.getDefaultToolkit().createImage(imgUrl);
        }
        return null;
    }

    /**
     * Read image from file in classpath located in a directory /images from the classpath root.
     *
     * @param filename Filename of image
     * @return Image or null if no such image file was found
     */
    public static Image createImage(String filename) {
        URL imgUrl = IconResources.class.getResource("/images/" + filename);
        if (imgUrl != null) {
            return Toolkit.getDefaultToolkit().createImage(imgUrl);
        }
        return null;
    }
}

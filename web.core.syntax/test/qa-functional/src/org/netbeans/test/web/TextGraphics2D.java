/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.test.web;

import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;


/** Renders all drawString(...) into StringBuffer.
 *
 * @author Martin.Schovanek@sun.com
 */
public class TextGraphics2D extends Graphics2D {
    StringBuffer buf = new StringBuffer();
    Component dummy;
    
    /**
     * Creates a new instance of TextGraphics2D
     */
    public TextGraphics2D(Component component) {
        dummy = component;
    }
    
    public TextGraphics2D() {
        this(null);
    }
    
    public String getText() {
        return buf.toString();
    }
    
    public String getTextUni() {
        int start;
        StringBuffer buf = new StringBuffer().append(this.buf);
        while ((start=buf.indexOf("  ")) > -1) {
            buf.deleteCharAt(start);
        }
        return buf.toString();
    }
    
    public void clearRect(int param, int param1, int param2, int param3) {
    }
    
    public void clipRect(int param, int param1, int param2, int param3) {
    }
    
    public void copyArea(int param, int param1, int param2, int param3,
            int param4, int param5) {
    }
    
    public Graphics create() {
        return new TextGraphics2D(dummy);
    }
    
    public void dispose() {
    }
    
    public void drawArc(int param, int param1, int param2, int param3,
            int param4, int param5) {
    }
    
    public boolean drawImage(Image image, int param, int param2,
            ImageObserver imageObserver) {
        return true;
    }
    
    public boolean drawImage(Image image, int param, int param2, Color color,
            ImageObserver imageObserver) {
        return true;
    }
    
    public boolean drawImage(Image image, int param, int param2, int param3,
            int param4, ImageObserver imageObserver) {
        return true;
    }
    
    public boolean drawImage(Image image, int param, int param2, int param3,
            int param4, Color color, ImageObserver imageObserver) {
        return true;
    }
    
    public boolean drawImage(Image image, int param, int param2, int param3,
            int param4, int param5, int param6, int param7, int param8,
            ImageObserver imageObserver) {
        return true;
    }
    
    public boolean drawImage(Image image, int param, int param2, int param3,
            int param4, int param5, int param6, int param7, int param8,
            Color color, ImageObserver imageObserver) {
        return true;
    }
    
    public void drawLine(int param, int param1, int param2, int param3) {
    }
    
    public void drawOval(int param, int param1, int param2, int param3) {
    }
    
    public void drawPolygon(int[] values, int[] values1, int param) {
    }
    
    public void drawPolyline(int[] values, int[] values1, int param) {
    }
    
    public void drawRoundRect(int param, int param1, int param2, int param3,
            int param4, int param5) {
    }
    
    public void drawString(String str, int param, int param2) {
        buf.append(str);
    }
    
    public void drawString(AttributedCharacterIterator iterator,
            int param, int param2) {
        for (char c=iterator.first(); c != iterator.DONE; c=iterator.next()) {
            buf.append(c);
        }
    }
    
    public void fillArc(int param, int param1, int param2, int param3, int param4,
            int param5) {
    }
    
    public void fillOval(int param, int param1, int param2, int param3) {
    }
    
    public void fillPolygon(int[] values, int[] values1, int param) {
    }
    
    public void fillRect(int param, int param1, int param2, int param3) {
    }
    
    public void fillRoundRect(int param, int param1, int param2, int param3,
            int param4, int param5) {
    }
    
    public Shape getClip() {
        return null;
    }
    
    public Rectangle getClipBounds() {
        return null;
    }
    
    public Color getColor() {
        return null;
    }
    
    public Font getFont() {
        if (dummy == null) return null;
        return dummy.getFont();
    }
    
    public FontMetrics getFontMetrics(Font font) {
        if (dummy == null) return null;
        return dummy.getFontMetrics(dummy.getFont());
    }
    
    public void setClip(Shape shape) {
    }
    
    public void setClip(int param, int param1, int param2, int param3) {
    }
    
    public void setColor(Color color) {
    }
    
    public void setFont(Font font) {
    }
    
    public void setPaintMode() {
    }
    
    public void setXORMode(Color color) {
    }
    
    public void translate(int param, int param1) {
    }
    
    public void addRenderingHints(Map map) {
    }
    
    public void clip(Shape shape) {
    }
    
    public void draw(Shape shape) {
    }
    
    public void drawGlyphVector(GlyphVector glyphVector, float param,
            float param2) {
    }
    
    public boolean drawImage(Image image, AffineTransform affineTransform,
            ImageObserver imageObserver) {
        return true;
    }
    
    public void drawImage(BufferedImage bufferedImage,
            BufferedImageOp bufferedImageOp, int param, int param3) {
    }
    
    public void drawRenderableImage(RenderableImage renderableImage,
            AffineTransform affineTransform) {
    }
    
    public void drawRenderedImage(RenderedImage renderedImage,
            AffineTransform affineTransform) {
    }
    
    public void drawString(String str, float param, float param2) {
        buf.append(str);
    }
    
    public void drawString(AttributedCharacterIterator iterator,
            float param, float param2) {
        for (char c=iterator.first(); c != iterator.DONE; c=iterator.next()) {
            buf.append(c);
        }
    }
    
    public void fill(Shape shape) {
    }
    
    public Color getBackground() {
        if (dummy == null) return null;
        return dummy.getBackground();
    }
    
    public Composite getComposite() {
        return null;
    }
    
    public GraphicsConfiguration getDeviceConfiguration() {
        return null;
    }
    
    public FontRenderContext getFontRenderContext() {
        return null;
    }
    
    public Paint getPaint() {
        return null;
    }
    
    public Object getRenderingHint(RenderingHints.Key key) {
        return null;
    }
    
    public RenderingHints getRenderingHints() {
        return null;
    }
    
    public Stroke getStroke() {
        return null;
    }
    
    public AffineTransform getTransform() {
        return null;
    }
    
    public boolean hit(Rectangle rectangle, Shape shape, boolean param) {
        return true;
    }
    
    public void rotate(double param) {
    }
    
    public void rotate(double param, double param1, double param2) {
    }
    
    public void scale(double param, double param1) {
    }
    
    public void setBackground(Color color) {
    }
    
    public void setComposite(Composite composite) {
    }
    
    public void setPaint(Paint paint) {
    }
    
    public void setRenderingHint(RenderingHints.Key key, Object obj) {
    }
    
    public void setRenderingHints(Map map) {
    }
    
    public void setStroke(Stroke stroke) {
    }
    
    public void setTransform(AffineTransform affineTransform) {
    }
    
    public void shear(double param, double param1) {
    }
    
    public void transform(AffineTransform affineTransform) {
    }
    
    public void translate(double param, double param1) {
    }
}

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */

package org.netbeans.core.output2;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.core.output2.ui.AbstractOutputPane;

/**
 * Component that draws controls for expanding and collapsing of folds.
 *
 * @author jhavlin
 */
public class FoldingSideBar extends JComponent {

    private static final Logger LOG =
            Logger.getLogger(FoldingSideBar.class.getName());
    private final int BAR_WIDTH = 15;

    private final JEditorPane textView;
    private AbstractLines lines;
    private int charsPerLine = 80;
    private boolean wrapped;

    public FoldingSideBar(JEditorPane textView, AbstractOutputPane outputPane) {
        this.textView = textView;
        this.lines = getLines();
        textView.addPropertyChangeListener("document", //NOI18N
                new PropertyChangeListener() {

                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        FoldingSideBar.this.lines = getLines();
                    }
                });
        setMinimumSize(new Dimension(BAR_WIDTH, 0));
        setPreferredSize(new Dimension(BAR_WIDTH, 1024));
        setMaximumSize(new Dimension(BAR_WIDTH, Integer.MAX_VALUE));
        wrapped = outputPane.isWrapped();
        addMouseListener(new FoldingMouseListener());
        addMouseMotionListener(new FoldingMouseListener()); //TODO one is enough
    }

    private AbstractLines getLines() {
        Document doc = textView.getDocument();
        if (doc instanceof OutputDocument) {
            Lines l = ((OutputDocument) doc).getLines();
            if (l instanceof AbstractLines) {
                return (AbstractLines) l;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        Rectangle cp = g.getClipBounds();
        g.setColor(getBackground());
        g.fillRect(cp.x, cp.y, cp.width, cp.height);
        g.setColor(getForeground());
        FontMetrics fontMetrics = textView.getFontMetrics(textView.getFont());
        int lineHeight = fontMetrics.getHeight();
        int descent = fontMetrics.getDescent();
        int offset = 0;
        try {
            Rectangle modelToView = textView.modelToView(0);
            offset = modelToView.y;
        } catch (BadLocationException ex) {
            LOG.log(Level.INFO, null, ex);
        }
        offset += lineHeight - fontMetrics.getAscent();

        int size = lines.getLineCount();
        int logLine = 0; // logical line (including wrapped lines)
        int nextLogLine;
        for (int i = 0; i < size - 1; i++) {
            if (!lines.isVisible(i)) {
                continue;
            }
            nextLogLine = findLogicalLineIndex(findNextVisibleLine(i), size);
            drawLineGraphics(g, i, logLine, nextLogLine, offset, lineHeight,
                    descent);
            logLine = nextLogLine;
        }
    }

    /**
     * @param g Graphics to draw into.
     * @param line Physical line index.
     * @param logLine Logical visible line index.
     * @param nextLogLine Logical index of the next visible line.
     * @param offset Y offset of the first line (pixels).
     * @param lineHeight Height of line (pixels).
     * @param descent Descent of font metrics (pixels).
     */
    private void drawLineGraphics(Graphics g, int line, int logLine,
            int nextLogLine, int offset, int lineHeight, int descent) {

        int currOffset = lines.getFoldOffsets().get(line);
        int nextOffset = line + 1 < lines.getFoldOffsets().size()
                ? lines.getFoldOffsets().get(line + 1) : 0;

        int startY = logLine * lineHeight + offset;
        int endY = nextLogLine * lineHeight + offset;
        if (nextOffset == 1) {
            drawButton(g, startY, endY, line);
        } else if (currOffset != 0 && currOffset + 1 == nextOffset) {
            g.drawLine(7, startY, 7, endY);
        } else if (currOffset > 0 && nextOffset == 0) {
            drawFoldEnd(g, startY, lineMid(endY, lineHeight, descent));
        } else if (currOffset > 0 && nextOffset > 0) {
            drawNestedFoldEnd(g, startY, endY,
                    lineMid(endY, lineHeight, descent));
        }
    }

    private static int lineMid(int lineEndY, int lineHeight, int descent) {
        return lineEndY - (lineHeight / 2) - descent;
    }

    /**
     * Draw graphics for a line that is at the start of a fold, which includes
     * expand/collapse button.
     *
     * @param g
     * @param lineStartY Y coordinate of the start of the line.
     * @param lineEndY Y coordinate of the end of the line.
     */
    private void drawButton(Graphics g, int lineStartY, int lineEndY, int line) {
        boolean collapsed = !lines.isVisible(line + 1);
        g.drawRect(2, lineStartY, 10, 10);
        g.drawLine(5, lineStartY + 5, 9, lineStartY + 5);
        if (collapsed) {
            g.drawLine(7, lineStartY + 3, 7, lineStartY + 7);
        }
        if (lineEndY > lineStartY + 10
                && (!collapsed || isLastVisibleLineInFold(line))) {
            g.drawLine(7, lineStartY + 10, 7, lineEndY);
        }
    }

    private boolean isLastVisibleLineInFold(int line) {
        if (lines.getFoldOffsets().get(line) > 0) {
            int visibleLine = lines.realToVisibleLine(line);
            int nextVisibleRealIndex = lines.visibleToRealLine(visibleLine + 1);
            return lines.getFoldOffsets().get(nextVisibleRealIndex) > 0;
        } else {
            return false;
        }
    }

    /**
     * Draw graphics for a line at the end of a nested fold.
     *
     * @param g
     * @param lineStartY Y coordinate of the start of the line.
     * @param lineMid Y coordinate of the middle of the last logical line.
     */
    private void drawNestedFoldEnd(Graphics g, int lineStartY, int lineEndY,
            int lineMid) {
        g.drawLine(7, lineStartY, 7, lineEndY);
        g.drawLine(7, lineMid, 11, lineMid);
    }

    /**
     * Draw graphics for a line at the end of a fold.
     *
     * @param g
     * @param lineStartY Y coordinate of the start of the line.
     * @param lineMid Y coordinate of the middle of the last logical line.
     */
    private void drawFoldEnd(Graphics g, int lineStartY, int lineMid) {
        g.drawLine(7, lineStartY, 7, lineMid);
        g.drawLine(7, lineMid, 11, lineMid);
    }

    /**
     * That logical line index for physical line {@code physicalLineIndex}. If
     * the physical line index is bigger or equal to count of physical lines,
     * return total count of logical lines.
     *
     * @param physicalLineIndex Index of physical (not wrapped) visible line.
     * @param size Total count of physical visible lines.
     */
    private int findLogicalLineIndex(int physicalLineIndex, int size) {
        if (wrapped) {
            if (physicalLineIndex < size) {
                return lines.getLogicalLineCountAbove(
                        physicalLineIndex, charsPerLine);
            } else {
                return lines.getLogicalLineCountIfWrappedAt(charsPerLine);
            }
        } else {
            return physicalLineIndex;
        }
    }

    /**
     * Find next visible line below a line.
     *
     * @param physicalLine Physical index of a visible line.
     * @return Physical index of the nearest visible line below
     * {@code physicalLine}.
     */
    private int findNextVisibleLine(int physicalLine) {
        int visibleLineIndex = lines.realToVisibleLine(physicalLine);
        if (visibleLineIndex < 0) {
            return lines.getVisibleLineCount() - 1;
        }
        if (visibleLineIndex + 1 < lines.getVisibleLineCount()) {
            return lines.visibleToRealLine(visibleLineIndex + 1);
        } else {
            return lines.getVisibleLineCount() - 1;
        }
    }

    public void setWrapped(boolean wrapped) {
        this.wrapped = wrapped;
        repaint();
    }

    public void setCharsPerLine(int charsPerLine) {
        this.charsPerLine = charsPerLine;
        repaint();
    }

    private class FoldingMouseListener extends MouseAdapter {

        @Override
        public void mouseMoved(MouseEvent e) {
            int physicalRealLine = getLineForEvent(e);            
            if (isFoldStartLine(physicalRealLine)) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            } else {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            int physicalRealLine = getLineForEvent(e);
            if (isFoldStartLine(physicalRealLine)) {
                if (lines.isVisible(physicalRealLine + 1)) {
                    lines.hideFold(physicalRealLine);
                } else {
                    lines.showFold(physicalRealLine);
                }
            }
        }

        private boolean isFoldStartLine(int physicalLine) {
            return physicalLine >= 0
                    && physicalLine + 1 < lines.getFoldOffsets().size()
                    && lines.getFoldOffsets().get(physicalLine + 1) == 1;
        }

        private int getLineForEvent(MouseEvent e) {
            // TODO refactor, the same code as in paint()
            FontMetrics fontMetrics = textView.getFontMetrics(textView.getFont());
            int lineHeight = fontMetrics.getHeight();
            int offset = 0;
            try {
                Rectangle modelToView = textView.modelToView(0);
                offset = modelToView.y;
            } catch (BadLocationException ex) {
                LOG.log(Level.INFO, null, ex);
            }
            offset += lineHeight - fontMetrics.getAscent();
            // end TODO
            int logicalLine = (e.getY() - offset) / lineHeight;
            final int physicalLine;
            if (wrapped) {
                int[] info = new int[]{logicalLine, 0, 0};
                lines.toPhysicalLineIndex(info, charsPerLine);
                physicalLine = info[0];
            } else {
                physicalLine = logicalLine < lines.getVisibleLineCount()
                        ? lines.visibleToRealLine(logicalLine)
                        : -1;
            }
            return physicalLine;
        }
    }
}

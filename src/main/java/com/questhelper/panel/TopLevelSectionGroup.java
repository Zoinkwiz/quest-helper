/*
 * Copyright (c) 2025, Zoinkwiz <https://github.com/Zoinkwiz>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.panel;

import lombok.Getter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TopLevelSectionGroup extends JPanel
{
    private static final int OUTER_VGAP = 8;
    private static final int OUTER_HGAP = 0;

    private static final int INNER_PAD_TOP    = 10;
    private static final int INNER_PAD_RIGHT  = 3;
    private static final int INNER_PAD_BOTTOM = 1;
    private static final int INNER_PAD_LEFT   = 3;


    private static final float TITLE_SCALE = 1.05f;
    private static final int   TITLE_BOTTOM_GAP = 6;

    private static final int RADIUS = 8;
    private static final int BORDER_THICK_OUTER = 1;
    private static final int BORDER_THICK_INNER = 1;
    private static final int BORDER_INNER_INSET = 1;

    private static final Color BG_FRAME =
            mix(ColorScheme.DARK_GRAY_HOVER_COLOR, ColorScheme.DARKER_GRAY_COLOR, 0.55f);
    private static final Color BORDER_OUTER =
            mix(ColorScheme.DARK_GRAY_HOVER_COLOR, Color.BLACK, 0.45f);
    private static final Color BORDER_INNER =
            mix(ColorScheme.BORDER_COLOR, Color.BLACK, 0.15f);

    @Getter
    private final JPanel contentPanel;

    public TopLevelSectionGroup(String title)
    {
        String title1 = title == null ? "" : title.trim();

        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(OUTER_VGAP, OUTER_HGAP, OUTER_VGAP, OUTER_HGAP));

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBorder(new EmptyBorder(
                INNER_PAD_TOP, INNER_PAD_LEFT, INNER_PAD_BOTTOM, INNER_PAD_RIGHT
        ));

        JLabel titleLabel;
        if (!title1.isEmpty())
        {
            titleLabel = new JLabel(title1);
            Font base = FontManager.getRunescapeBoldFont();
            titleLabel.setFont(base.deriveFont(base.getSize2D() * TITLE_SCALE));
            titleLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
            titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            titleLabel.setOpaque(false);

            inner.add(titleLabel);
            inner.add(Box.createVerticalStrut(TITLE_BOTTOM_GAP));
        }

        contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        inner.add(contentPanel);

        add(inner, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        try
        {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Insets out = getInsets();
            int x = out.left;
            int y = out.top;
            int w = getWidth()  - out.left - out.right;
            int h = getHeight() - out.top  - out.bottom;

            // Frame background
            g2.setColor(BG_FRAME);
            g2.fillRoundRect(x, y, w, h, RADIUS, RADIUS);

            // Outer border
            if (BORDER_THICK_OUTER > 0)
            {
                g2.setColor(BORDER_OUTER);
                g2.setStroke(new BasicStroke(BORDER_THICK_OUTER));
                g2.drawRoundRect(x, y, w - 1, h - 1, RADIUS, RADIUS);
            }

            if (BORDER_THICK_INNER > 0)
            {
                int ix = x + BORDER_INNER_INSET + BORDER_THICK_OUTER;
                int iy = y + BORDER_INNER_INSET + BORDER_THICK_OUTER;
                int iw = w - 1 - 2 * (BORDER_INNER_INSET + BORDER_THICK_OUTER);
                int ih = h - 1 - 2 * (BORDER_INNER_INSET + BORDER_THICK_OUTER);

                if (iw > 0 && ih > 0)
                {
                    g2.setColor(BORDER_INNER);
                    g2.setStroke(new BasicStroke(BORDER_THICK_INNER));
                    g2.drawRoundRect(ix, iy, iw, ih, Math.max(0, RADIUS - 2), Math.max(0, RADIUS - 2));
                }
            }
        }
        finally
        {
            g2.dispose();
        }
    }

    private static Color mix(Color a, Color b, double t)
    {
        float clamped = (float)Math.max(0.0, Math.min(1.0, t));
        int r = Math.round(a.getRed()   * (1 - clamped) + b.getRed()   * clamped);
        int g = Math.round(a.getGreen() * (1 - clamped) + b.getGreen() * clamped);
        int bl= Math.round(a.getBlue()  * (1 - clamped) + b.getBlue()  * clamped);
        return new Color(r, g, bl);
    }
}
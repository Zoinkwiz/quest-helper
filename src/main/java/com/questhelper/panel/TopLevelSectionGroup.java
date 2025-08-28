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
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class TopLevelSectionGroup extends JPanel
{
    @Getter
    private final JPanel contentPanel;

    public TopLevelSectionGroup(String title)
    {
        Border outer = new LineBorder(ColorScheme.DARK_GRAY_HOVER_COLOR, 1, true);
        Border gap = new EmptyBorder(1, 1, 1, 1);
        Border innerBorder = new LineBorder(ColorScheme.BORDER_COLOR, 1, true);
        Border doubleRounded = BorderFactory.createCompoundBorder(outer, BorderFactory.createCompoundBorder(gap, innerBorder));

        TitledBorder titledBorder = new TightTitledBorder(doubleRounded, "", TitledBorder.LEFT, TitledBorder.TOP,
                FontManager.getRunescapeBoldFont(),  ColorScheme.LIGHT_GRAY_COLOR);

        String title1 = title == null ? "" : title.trim();

        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(0, 0, 0, 0));

        JLabel titleLabel = new JLabel(title1);
        Font base = FontManager.getRunescapeBoldFont();
        titleLabel.setFont(base.deriveFont(base.getSize2D()));
        titleLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setOpaque(false);

        TightTitledBorder framed = new TightTitledBorder(
                titledBorder,
                title1,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                base,
                ColorScheme.LIGHT_GRAY_COLOR
        );


        contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPanel.setBorder(framed);

        add(contentPanel, BorderLayout.CENTER);
    }
}
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

import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;

class TightTitledBorder extends TitledBorder
{
    TightTitledBorder(Border border, String title, int justification, int position, Font font, Color color)
    {
        super(border, title, justification, position, font, color);
    }


    @Override
    public Insets getBorderInsets(Component c, Insets insets)
    {
        Insets b = (border != null) ? border.getBorderInsets(c) : new Insets(0, 0, 0, 0);

        Font f = getTitleFont();
        if (f == null) f = c.getFont();
        FontMetrics fm = c.getFontMetrics(f);
        int ascent = (title == null || title.isEmpty()) ? 0 : fm.getAscent();

        insets.left   = b.left;
        insets.right  = b.right;
        insets.bottom = b.bottom;

        insets.top = Math.max(b.top, ascent + 2);

        return insets;
    }
}

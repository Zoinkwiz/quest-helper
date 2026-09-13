/*
 * Copyright (c) 2024, pajlada <https://github.com/pajlada>
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
package com.questhelper.util;

import com.questhelper.panel.JGenerator;
import lombok.Getter;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.HashMap;

public class Fonts
{
	// Note: offset 3 is skipped for LARGE as it produces a glyph-hinting artifact on the system font (e.g. "0" renders poorly at that size).
	private static final Font baseFont;

	@Getter
	private static final float defaultSize;

	@Getter
	private static Font originalFont;
	@Getter
	private static Font underlinedFont;

	static
	{
		var label = JGenerator.makeJTextArea();
		baseFont = label.getFont();
		defaultSize = baseFont.getSize2D();
		originalFont = baseFont;
		var attributes = new HashMap<TextAttribute, Object>(baseFont.getAttributes());
		attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		underlinedFont = baseFont.deriveFont(attributes);
	}

	/**
	 * Update the global font size used by the sidebar panel.
	 * Call this when the config changes.
	 */
	public static void setSize(int size)
	{
		var baseAttributes = new HashMap<TextAttribute, Object>(baseFont.getAttributes());
		baseAttributes.put(TextAttribute.SIZE, (float) size);
		baseAttributes.put(TextAttribute.KERNING, TextAttribute.KERNING_ON);
		originalFont = baseFont.deriveFont(baseAttributes);

		var underlineAttributes = new HashMap<TextAttribute, Object>(originalFont.getAttributes());
		underlineAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		underlinedFont = originalFont.deriveFont(underlineAttributes);
	}
}

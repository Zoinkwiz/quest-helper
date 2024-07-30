package com.questhelper.util;

import lombok.Getter;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.HashMap;

public class Fonts
{
	@Getter
	private static final Font originalFont;
	@Getter
	private static final Font underlinedFont;

	static
	{
		var label = new JLabel();
		originalFont = label.getFont();
		var attributes = new HashMap<TextAttribute, Object>(originalFont.getAttributes());
		attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		underlinedFont = originalFont.deriveFont(attributes);
	}
}

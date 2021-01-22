/*
 *
 *  * Copyright (c) 2021, Senmori
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  *
 *  * 1. Redistributions of source code must retain the above copyright notice, this
 *  *    list of conditions and the following disclaimer.
 *  * 2. Redistributions in binary form must reproduce the above copyright notice,
 *  *    this list of conditions and the following disclaimer in the documentation
 *  *    and/or other materials provided with the distribution.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package com.questhelper.panel.components;

import com.questhelper.IconUtil;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;
import javax.swing.JLabel;
import javax.swing.JPanel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HeaderPanel extends JPanel implements MouseListener
{
	private static final BufferedImage COLLAPSED, EXPANDED;

	static
	{
		COLLAPSED = IconUtil.COLLAPSED.getImage();
		EXPANDED = IconUtil.EXPANDED.getImage();
	}
	@Getter
	@Setter(AccessLevel.PRIVATE)
	private boolean isCollapsed = false;
	private final Consumer<Boolean> clickConsumer;
	@Getter
	private JLabel headerLabel;
	private final int OFFSET = 30, PAD = 5;

	public HeaderPanel(JLabel headerLabel, Consumer<Boolean> onClickConsumer) {
		this.clickConsumer = onClickConsumer;
		addMouseListener(this);
		this.headerLabel = headerLabel;
		setFont(headerLabel.getFont());
		setPreferredSize(headerLabel.getPreferredSize());
		setBackground(headerLabel.getBackground());
		setForeground(headerLabel.getForeground());
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		int h = getHeight();
		if (isCollapsed())
		{
			g2.drawImage(COLLAPSED, PAD, 0, h, h, this);
		}
		else
		{
			g2.drawImage(EXPANDED, PAD, 0, h, h, this);
		}
		g2.setFont(getFont());
		FontRenderContext frc = g2.getFontRenderContext();
		LineMetrics lm = getFont().getLineMetrics(headerLabel.getText(), frc);
		float height = lm.getAscent() + lm.getDescent();
		float y = (h + height) / 2 - lm.getDescent();
		g2.drawString(headerLabel.getText(), OFFSET, y);
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		if (e.getClickCount() % 2 == 0) // only collapse on double click
		{
			setCollapsed(!isCollapsed());
			clickConsumer.accept(isCollapsed());
			log.debug(headerLabel.getText() + " is now " + (isCollapsed() ? "collapsed" : "expanded"));
		}
	}

	@Override
	public void mousePressed(MouseEvent e)
	{

	}

	@Override
	public void mouseReleased(MouseEvent e)
	{

	}

	@Override
	public void mouseEntered(MouseEvent e)
	{

	}

	@Override
	public void mouseExited(MouseEvent e)
	{

	}
}

/*
 * Copyright (c) 2018, Psikoi <https://github.com/psikoi>
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
package com.questhelper.panel.skillfiltering;

import com.questhelper.QuestHelperConfig;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.Border;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.ColorScheme;

public class SkillIconButton extends JButton
{
	private static final Border ICON_BORDER = BorderFactory
		.createEmptyBorder(5, 10, 5, 10);

	final Color FILTERED_COLOR = Color.RED.darker();
	final Color NOT_FILTERED_COLOR = ColorScheme.DARKER_GRAY_COLOR;
	public SkillIconButton(ImageIcon icon, ConfigManager configManager, String skillName)
	{
		super();
		setIcon(icon);
		setOpaque(true);
		setVerticalAlignment(SwingConstants.CENTER);
		setHorizontalAlignment(SwingConstants.CENTER);
		setBorder(ICON_BORDER);
		setToolTipText("Hide quests that'd require or reward experience in " + skillName);

		if (isFiltered(configManager, skillName))
		{
			setToolTipText("Show quests that'd require or reward experience in " + skillName);
			setBackground(FILTERED_COLOR);
		}
		else
		{
			setToolTipText("Hide quests that'd require or reward experience in " + skillName);
			setBackground(NOT_FILTERED_COLOR);
		}

		addMouseListener(new MouseAdapter()
		{
			private Color currentColor = getBackground();

			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{

				if (isFiltered(configManager, skillName))
				{
					currentColor = NOT_FILTERED_COLOR;
					setBackground(currentColor);
					configManager.setConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, "skillfilter" + skillName, "false");
					setToolTipText("Hide quests that'd require or reward experience in " + skillName);
				}
				else
				{
					currentColor = FILTERED_COLOR;
					setBackground(currentColor);
					configManager.setConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, "skillfilter" + skillName, "true");
					setToolTipText("Show quests that'd require or reward experience in " + skillName);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				setBackground(currentColor.brighter());
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				setBackground(currentColor);
			}
		});
	}

	public boolean isFiltered(ConfigManager configManager, String skillName)
	{
		String isFiltered = configManager.getConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, "skillfilter" + skillName);
		return "true".equals(isFiltered);
	}
}

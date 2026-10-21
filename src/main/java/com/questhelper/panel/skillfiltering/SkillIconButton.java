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
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SkillIconButton extends JButton
{
	private static final Border ICON_BORDER = BorderFactory
		.createEmptyBorder(5, 10, 5, 10);

	final Color NOT_FILTERED_COLOR = ColorScheme.DARKER_GRAY_COLOR;

	public SkillIconButton(ImageIcon icon, ConfigManager configManager, String skillName)
	{
		this(icon, configManager, "skillfilter" + skillName, Color.RED.darker(),
			"Hide quests that'd require or reward experience in " + skillName,
			"Show quests that'd require or reward experience in " + skillName);
	}

	public SkillIconButton(ImageIcon icon, ConfigManager configManager, String configKey, Color filteredColor,
						   String enableTooltip, String disableTooltip)
	{
		super();
		setIcon(icon);
		setOpaque(true);
		setVerticalAlignment(SwingConstants.CENTER);
		setHorizontalAlignment(SwingConstants.CENTER);
		setBorder(ICON_BORDER);

		if (isFiltered(configManager, configKey))
		{
			setToolTipText(disableTooltip);
			setBackground(filteredColor);
		}
		else
		{
			setToolTipText(enableTooltip);
			setBackground(NOT_FILTERED_COLOR);
		}

		addMouseListener(new MouseAdapter()
		{
			private Color currentColor = getBackground();

			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{

				if (isFiltered(configManager, configKey))
				{
					currentColor = NOT_FILTERED_COLOR;
					setBackground(currentColor);
					configManager.setConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, configKey, "false");
					setToolTipText(enableTooltip);
				}
				else
				{
					currentColor = filteredColor;
					setBackground(currentColor);
					configManager.setConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, configKey, "true");
					setToolTipText(disableTooltip);
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

	public boolean isFiltered(ConfigManager configManager, String configKey)
	{
		String isFiltered = configManager.getConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, configKey);
		return "true".equals(isFiltered);
	}
}

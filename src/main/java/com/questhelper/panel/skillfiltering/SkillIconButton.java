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
import com.questhelper.config.RewardFiltering;
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

	final Color FILTERED_COLOR = Color.RED.darker();
	final Color REWARD_FILTERED_COLOR = Color.GREEN.darker().darker();
	final Color NOT_FILTERED_COLOR = ColorScheme.DARKER_GRAY_COLOR;

	private final ConfigManager configManager;
	private final String skillName;
	private Color currentColor;

	public SkillIconButton(ImageIcon icon, ConfigManager configManager, String skillName)
	{
		super();
		this.configManager = configManager;
		this.skillName = skillName;
		setIcon(icon);
		setOpaque(true);
		setVerticalAlignment(SwingConstants.CENTER);
		setHorizontalAlignment(SwingConstants.CENTER);
		setBorder(ICON_BORDER);
		updateAppearance();

		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				if (isEnabled("skillfilter"))
				{
					setConfig("skillfilter", false);
					setConfig(RewardFiltering.CONFIG_KEY_PREFIX, true);
				}
				else if (isEnabled(RewardFiltering.CONFIG_KEY_PREFIX))
				{
					setConfig(RewardFiltering.CONFIG_KEY_PREFIX, false);
				}
				else
				{
					setConfig("skillfilter", true);
				}
				updateAppearance();
				setBackground(currentColor.brighter());
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

	private void updateAppearance()
	{
		if (isEnabled("skillfilter"))
		{
			currentColor = FILTERED_COLOR;
			setToolTipText("Only show quests that reward experience in " + skillName);
		}
		else if (isEnabled(RewardFiltering.CONFIG_KEY_PREFIX))
		{
			currentColor = REWARD_FILTERED_COLOR;
			setToolTipText("Stop filtering by " + skillName);
		}
		else
		{
			currentColor = NOT_FILTERED_COLOR;
			setToolTipText("Hide quests that'd require or reward experience in " + skillName);
		}
		setBackground(currentColor);
	}

	private boolean isEnabled(String configKeyPrefix)
	{
		return "true".equals(configManager.getConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, configKeyPrefix + skillName));
	}

	private void setConfig(String configKeyPrefix, boolean enabled)
	{
		configManager.setConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, configKeyPrefix + skillName, String.valueOf(enabled));
	}
}

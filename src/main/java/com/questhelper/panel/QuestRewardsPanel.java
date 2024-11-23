/*
 * Copyright (c) 2021, Zoinkwiz
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
package com.questhelper.panel;

import com.questhelper.rewards.Reward;
import com.questhelper.util.Fonts;
import net.runelite.client.ui.ColorScheme;
import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class QuestRewardsPanel extends JPanel
{
	private final JTextArea rewardsText = new JTextArea();

	public QuestRewardsPanel()
	{
		setBackground(ColorScheme.DARKER_GRAY_COLOR);
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(0, 0, 10, 0));

		var headerPanel = QuestRequirementsPanel.createHeader("Rewards:");

		add(headerPanel, BorderLayout.NORTH);

		var rewardsPanel = new JPanel();
		rewardsPanel.setLayout(new DynamicPaddedGridLayout(0, 1, 0, 1));
		rewardsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

		rewardsText.setLineWrap(true);
		rewardsText.setWrapStyleWord(true);
		rewardsText.setOpaque(false);
		rewardsText.setEditable(false);
		rewardsText.setFocusable(false);
		rewardsText.setBackground(javax.swing.UIManager.getColor("Label.background"));
		rewardsText.setFont(Fonts.getOriginalFont());
		rewardsText.setBorder(new EmptyBorder(0, 0, 0, 0));

		add(rewardsPanel, BorderLayout.CENTER);

		rewardsPanel.add(rewardsText);
	}

	public void setRewards(@Nullable List<Reward> rewards)
	{
		Reward lastReward = null;
		if (rewards != null && !rewards.isEmpty())
		{
			rewardsText.setForeground(Color.WHITE);

			var text = new StringBuilder();

			for (var reward : rewards)
			{
				if (lastReward != null)
				{
					text.append("\n");
					if (lastReward.rewardType() != reward.rewardType())
					{
						text.append("\n");
					}
				}
				lastReward = reward;

				text.append(reward.getDisplayText());
			}

			rewardsText.setText(text.toString());
		}
		else
		{
			rewardsText.setText("None");
			rewardsText.setForeground(Color.GRAY);
		}

		revalidate();
	}

	public void hideRewards()
	{
		rewardsText.setText("Hidden by the \"Hide quest rewards\" config");
		rewardsText.setForeground(Color.GRAY);

		revalidate();
	}
}

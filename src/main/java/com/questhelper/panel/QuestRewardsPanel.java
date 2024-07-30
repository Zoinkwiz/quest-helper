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
}

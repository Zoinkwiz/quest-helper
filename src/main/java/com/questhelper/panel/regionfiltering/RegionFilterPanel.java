/*
 * Copyright (c) 2026, Syrif <https://github.com/syrifgit>
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
package com.questhelper.panel.regionfiltering;

import com.questhelper.config.LeagueFiltering;
import com.questhelper.questinfo.LeagueRegion;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EnumSet;

public class RegionFilterPanel extends JPanel
{
	private final EnumSet<LeagueRegion> selectedRegions = EnumSet.noneOf(LeagueRegion.class);
	private final Runnable onChanged;
	private final SpriteManager spriteManager;
	public RegionFilterPanel(SpriteManager spriteManager, Runnable onChanged)
	{
		super();
		this.spriteManager = spriteManager;
		this.onChanged = onChanged;

		setBorder(new EmptyBorder(10, 10, 10, 10));
		setLayout(new GridLayout(0, 3, 7, 7));

		for (LeagueRegion region : LeagueRegion.values())
		{
			add(createRegionButton(region));
		}
	}

	private JButton createRegionButton(LeagueRegion region)
	{
		JButton button = new JButton();
		button.setOpaque(true);
		button.setFocusPainted(false);
		button.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		button.setToolTipText(region.getDisplayName());
		button.setText(region.getDisplayName());
		button.setFont(button.getFont().deriveFont(Font.PLAIN, 9f));
		button.setForeground(Color.GRAY);
		button.setMargin(new Insets(4, 2, 4, 2));

		// Load sprite async on client thread, replace text with icon when ready
		spriteManager.getSpriteAsync(region.getSpriteId(), 0, sprite -> {
			if (sprite != null)
			{
				ImageIcon selectedIcon = new ImageIcon(sprite);
				ImageIcon deselectedIcon = new ImageIcon(ImageUtil.alphaOffset(sprite, -180));
				button.putClientProperty("selectedIcon", selectedIcon);
				button.putClientProperty("deselectedIcon", deselectedIcon);
				SwingUtilities.invokeLater(() -> {
					button.setText(null);
					if (selectedRegions.contains(region))
					{
						button.setIcon(selectedIcon);
					}
					else
					{
						button.setIcon(deselectedIcon);
					}
					button.setPreferredSize(new Dimension(sprite.getWidth() + 10, sprite.getHeight() + 10));
					button.revalidate();
				});
			}
		});

		button.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (selectedRegions.contains(region))
				{
					selectedRegions.remove(region);
					updateButtonState(button, false);
				}
				else
				{
					selectedRegions.add(region);
					updateButtonState(button, true);
				}
				LeagueFiltering.setSelectedRegions(selectedRegions.isEmpty() ? null : EnumSet.copyOf(selectedRegions));
				onChanged.run();
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				button.setBackground(button.getBackground().brighter());
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				if (selectedRegions.contains(region))
				{
					button.setBackground(ColorScheme.BRAND_ORANGE);
				}
				else
				{
					button.setBackground(ColorScheme.DARKER_GRAY_COLOR);
				}
			}
		});

		return button;
	}

	private void updateButtonState(JButton button, boolean selected)
	{
		if (selected)
		{
			button.setBackground(ColorScheme.BRAND_ORANGE);
			ImageIcon selectedIcon = (ImageIcon) button.getClientProperty("selectedIcon");
			if (selectedIcon != null)
			{
				button.setIcon(selectedIcon);
			}
			else
			{
				button.setForeground(Color.WHITE);
			}
		}
		else
		{
			button.setBackground(ColorScheme.DARKER_GRAY_COLOR);
			ImageIcon deselectedIcon = (ImageIcon) button.getClientProperty("deselectedIcon");
			if (deselectedIcon != null)
			{
				button.setIcon(deselectedIcon);
			}
			else
			{
				button.setForeground(Color.GRAY);
			}
		}
	}

	public int getSelectedCount()
	{
		return selectedRegions.size();
	}

	/**
	 * Clears all selected regions and resets button states.
	 */
	public void clearSelection()
	{
		if (selectedRegions.isEmpty())
		{
			return;
		}
		selectedRegions.clear();
		LeagueFiltering.setSelectedRegions(null);
		for (Component comp : getComponents())
		{
			if (comp instanceof JButton)
			{
				updateButtonState((JButton) comp, false);
			}
		}
		onChanged.run();
	}
}

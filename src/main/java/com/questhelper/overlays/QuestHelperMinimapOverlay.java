package com.questhelper.overlays;

import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

public class QuestHelperMinimapOverlay extends Overlay
{
	private final QuestHelperPlugin plugin;

	@Inject
	public QuestHelperMinimapOverlay(QuestHelperPlugin plugin)
	{
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ALWAYS_ON_TOP);
		setPriority(OverlayPriority.HIGH);
		this.plugin = plugin;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		QuestHelper quest = plugin.getSelectedQuest();

		if (quest != null && quest.getCurrentStep() != null && quest.getCurrentStep().getActiveStep() != null)
		{
			quest.getCurrentStep().getActiveStep().makeDirectionOverlayHint(graphics, plugin);
		}
		return null;
	}
}

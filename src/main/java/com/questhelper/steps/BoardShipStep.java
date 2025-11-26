package com.questhelper.steps;

import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.ShipInPortRequirement;
import com.questhelper.requirements.util.Port;
import lombok.NonNull;
import net.runelite.api.gameval.ObjectID;
import net.runelite.client.ui.overlay.components.PanelComponent;
import java.util.ArrayList;
import java.util.List;

/**
 * Redirects you to board your ship, at the port you left it at.
 */
public class BoardShipStep extends ConditionalStep
{
	public BoardShipStep(QuestHelper questHelper, Requirement... requirements)
	{
		super(questHelper, new ObjectStep(questHelper, ObjectID.SAILING_GANGPLANK_EMBARK, Port.PORT_SARIM.getGangplankLocation(), "Board your ship in Port Sarim.", requirements), "Board your ship.");
		for(Port port: Port.values()){
			this.addStep(new ShipInPortRequirement(port), new ObjectStep(questHelper, ObjectID.SAILING_GANGPLANK_EMBARK, port.getGangplankLocation(), "Board your ship in " + port.getName() + ".", requirements));
		}
	}

	@Override
	public void makeOverlayHint(PanelComponent panelComponent, QuestHelperPlugin plugin, @NonNull List<String> additionalText, @NonNull List<Requirement> additionalRequirements)
	{
		List<Requirement> allRequirements = new ArrayList<>(additionalRequirements);
		allRequirements.addAll(requirements);

		List<String> allAdditionalText = new ArrayList<>(additionalText);

		if (currentStep != null)
		{
			currentStep.makeOverlayHint(panelComponent, plugin, allAdditionalText, allRequirements);
		}
	}
}

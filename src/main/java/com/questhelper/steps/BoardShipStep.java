package com.questhelper.steps;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.ShipInPortRequirement;
import com.questhelper.requirements.util.Port;
import net.runelite.api.gameval.ObjectID;

/**
 * Redirects you to board your ship, at the port you left it at.
 */
public class BoardShipStep extends ConditionalStep
{
	public BoardShipStep(QuestHelper questHelper, Requirement... requirements)
	{
		super(questHelper, new ObjectStep(questHelper, ObjectID.SAILING_GANGPLANK_EMBARK, Port.PORT_SARIM.getGangplankLocation(), "Board your ship in Port Sarim.", requirements));
		for(Port port: Port.values()){
			this.addStep(new ShipInPortRequirement(port), new ObjectStep(questHelper, ObjectID.SAILING_GANGPLANK_EMBARK, port.getGangplankLocation(), "Board your ship in " + port.getName() + ".", requirements));
		}
	}
}

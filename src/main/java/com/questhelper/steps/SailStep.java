package com.questhelper.steps;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.ShipAtDockRequirement;
import com.questhelper.requirements.util.Port;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import net.runelite.api.coords.WorldPoint;

public class SailStep extends DetailedQuestStep
{
	private final ZoneRequirement zoneReq;

	public SailStep(QuestHelper questHelper, Port toPort){
		this(questHelper, new ShipAtDockRequirement(toPort));
	}
	public SailStep(QuestHelper questHelper, ShipAtDockRequirement toPort)
	{
		super(questHelper, "Sail to " + toPort.getPort().getName());
		Zone zone = toPort.getPort().getDockZone();
		setHighlightZone(zone);
		this.zoneReq = new ZoneRequirement(zone);
		setWorldPoint(toPort.getPort().getBuoy());
	}
	public SailStep(QuestHelper questHelper, Port toPort, Requirement... requirements){
		this(questHelper, new ShipAtDockRequirement(toPort), requirements);
	}
	public SailStep(QuestHelper questHelper, ShipAtDockRequirement toPort, Requirement... requirements)
	{
		this(questHelper,toPort);
		this.addRequirement(requirements);
	}
	public SailStep(QuestHelper questHelper, WorldPoint toPoint)
	{
		super(questHelper, "Sail to the location on your map");
		Zone zone = new Zone(toPoint.dx(-5).dy(-5), toPoint.dx(5).dy(5));
		setHighlightZone(zone);
		this.zoneReq = new ZoneRequirement(zone);
		setWorldPoint(toPoint);
	}
	public SailStep(QuestHelper questHelper, WorldPoint toPoint, Requirement... requirements)
	{
		this(questHelper,toPoint);
		this.addRequirement(requirements);
	}

	public ZoneRequirement getZoneRequirement(){
		return zoneReq;
	}
}


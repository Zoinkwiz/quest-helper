package com.questhelper.steps;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.ShipInPortRequirement;
import com.questhelper.requirements.util.Port;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

public class SailStep extends DetailedQuestStep
{
	@Getter
	private final ZoneRequirement zoneRequirement;

	public SailStep(QuestHelper questHelper, Port toPort){
		this(questHelper, new ShipInPortRequirement(toPort));
	}
	public SailStep(QuestHelper questHelper, ShipInPortRequirement toPort)
	{
		super(questHelper, "Sail to " + toPort.getPort().getName());
		Zone zone = toPort.getPort().getDockZone();
		setHighlightZone(zone);
		this.zoneRequirement = new ZoneRequirement(zone);
		setWorldPoint(toPort.getPort().getBuoyLocation());
	}
	public SailStep(QuestHelper questHelper, Port toPort, Requirement... requirements){
		this(questHelper, new ShipInPortRequirement(toPort), requirements);
	}
	public SailStep(QuestHelper questHelper, ShipInPortRequirement toPort, Requirement... requirements)
	{
		this(questHelper,toPort);
		this.addRequirement(requirements);
	}
	public SailStep(QuestHelper questHelper, WorldPoint toPoint)
	{
		super(questHelper, "Sail to the location on your map");
		Zone zone = new Zone(toPoint.dx(-5).dy(-5), toPoint.dx(5).dy(5));
		setHighlightZone(zone);
		this.zoneRequirement = new ZoneRequirement(zone);
		setWorldPoint(toPoint);
	}
	public SailStep(QuestHelper questHelper, WorldPoint toPoint, Requirement... requirements)
	{
		this(questHelper,toPoint);
		this.addRequirement(requirements);
	}
}


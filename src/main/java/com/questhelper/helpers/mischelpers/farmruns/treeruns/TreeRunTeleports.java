package com.questhelper.helpers.mischelpers.farmruns.treeruns;

import com.questhelper.collections.ItemCollections;
import com.questhelper.requirements.item.ItemRequirement;
import lombok.Getter;
import net.runelite.api.gameval.ItemID;

@Getter
public class TreeRunTeleports
{
	private final ItemRequirement farmingGuildTeleport;
	private final ItemRequirement catherbyTeleport;
	private final ItemRequirement crystalTeleport;
	private final ItemRequirement varrockTeleport;
	private final ItemRequirement lumbridgeTeleport;
	private final ItemRequirement faladorTeleport;
	private final ItemRequirement fossilIslandTeleport;
	private final ItemRequirement auburnvaleTeleport;
	private final ItemRequirement kastoriTeleport;

	public TreeRunTeleports()
	{
		this.farmingGuildTeleport = new ItemRequirement("Farming Guild Teleport", ItemCollections.SKILLS_NECKLACES);
		this.crystalTeleport = new ItemRequirement("Crystal teleport", ItemCollections.TELEPORT_CRYSTAL);
		this.catherbyTeleport = new ItemRequirement("Catherby teleport", ItemID.LUNAR_TABLET_CATHERBY_TELEPORT);
		this.catherbyTeleport.addAlternates(ItemID.POH_TABLET_CAMELOTTELEPORT);
		this.varrockTeleport = new ItemRequirement("Varrock teleport", ItemID.POH_TABLET_VARROCKTELEPORT);
		this.lumbridgeTeleport = new ItemRequirement("Lumbridge teleport", ItemID.POH_TABLET_LUMBRIDGETELEPORT);
		this.faladorTeleport = new ItemRequirement("Falador teleport", ItemCollections.RING_OF_WEALTHS);
		this.faladorTeleport.addAlternates(ItemID.POH_TABLET_FALADORTELEPORT);
		this.fossilIslandTeleport = new ItemRequirement("Teleport to Fossil Island", ItemCollections.DIGSITE_PENDANTS);
		this.auburnvaleTeleport = new ItemRequirement("Auburnvale Teleport", ItemID.PENDANT_OF_ATES);
		this.auburnvaleTeleport.addAlternates(ItemCollections.FAIRY_STAFF);
		this.kastoriTeleport = new ItemRequirement("Kastori Teleport", ItemID.PENDANT_OF_ATES);
		this.kastoriTeleport.addAlternates(ItemCollections.FAIRY_STAFF);
	}
}

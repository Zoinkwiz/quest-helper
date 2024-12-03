package com.questhelper.collections;

import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.item.TeleportItemRequirement;
import com.questhelper.requirements.util.LogicType;
import net.runelite.api.ItemID;

public enum TeleportCollections
{
	BURTHORPE_TELEPORT()
		{
			public ItemRequirement getItemRequirement()
			{
				TeleportItemRequirement burthTele = new TeleportItemRequirement("Teleport to Burthorpe. Games necklace (Burthorpe [1]), minigame teleport (Burthorpe Games Room)",
					ItemCollections.GAMES_NECKLACES);
				return burthTele;
			}
		},
	VARROCK_TELEPORT()
		{
			public ItemRequirement getItemRequirement()
			{
				TeleportItemRequirement varrockTele = new TeleportItemRequirement("Teleport to Varrock. Varrock teleport tablet/spell, Chronicle, Ring of Wealth (Grand Exchange [2])",
					ItemID.VARROCK_TELEPORT);
				varrockTele.addAlternates(ItemID.CHRONICLE);
				varrockTele.addAlternates(ItemCollections.RING_OF_WEALTHS);

				ItemRequirement varrockRunes = new ItemRequirements("Varrock teleport runes",
					new ItemRequirement("Law rune", ItemID.LAW_RUNE, 1),
					new ItemRequirement("Air rune", ItemID.AIR_RUNE, 3),
					new ItemRequirement("Water rune", ItemID.WATER_RUNE, 1)
				);
				return new ItemRequirements(LogicType.OR, "Teleport to Varrock. Varrock teleport tablet/spell, Chronicle, Ring of Wealth (Grand Exchange [2])",
					varrockTele, varrockRunes);
			}
		},
	SOPHANEM_TELEPORT()
		{
			public ItemRequirement getItemRequirement()
			{
				TeleportItemRequirement sophTele = new TeleportItemRequirement("Teleport to Sophanem. Pharaoh's sceptre (Jalsavrah [1])",
					ItemCollections.PHAROAH_SCEPTRE);
				return sophTele;
			}
		},
	ARDOUGNE_TELEPORT()
		{
			@Override
			public ItemRequirement getItemRequirement()
			{
				TeleportItemRequirement ardougneTele = new TeleportItemRequirement("Teleport to Ardougne. Ardougne cloak, Ardougne teleport tablet/spell",
					ItemCollections.ARDY_CLOAKS);
				ardougneTele.addAlternates(ItemID.ARDOUGNE_TELEPORT);

				ItemRequirement ardougneRunes = new ItemRequirements("Ardougne teleport runes",
					new ItemRequirement("Law rune", ItemID.LAW_RUNE, 2),
					new ItemRequirement("Water rune", ItemID.WATER_RUNE, 2)
				);
				return new ItemRequirements(LogicType.OR, "Teleport to Ardougne. Ardougne cloak, Ardougne teleport tablet/spell", ardougneTele, ardougneRunes);
			}
		},
	FALADOR_TELEPORT()
		{
			@Override
			public ItemRequirement getItemRequirement()
			{
				TeleportItemRequirement faladorTele = new TeleportItemRequirement("Teleport to Falador. Falador teleport tablet/spell",
					ItemID.FALADOR_TELEPORT);

				ItemRequirement faladorRunes = new ItemRequirements("Falador teleport runes",
					new ItemRequirement("Law rune", ItemID.LAW_RUNE, 1),
					new ItemRequirement("Air rune", ItemID.AIR_RUNE, 3),
					new ItemRequirement("Water rune", ItemID.WATER_RUNE, 1)
				);

				return new ItemRequirements(LogicType.OR, "Teleport to Falador. Falador teleport tablet/spell", faladorTele, faladorRunes);
			}
		};

	public abstract ItemRequirement getItemRequirement();
}

package com.questhelper.collections;

import com.questhelper.requirements.item.TeleportItemRequirement;
import net.runelite.api.ItemID;

public enum TeleportCollections
{
	BURTHORPE_TELEPORT()
		{
			public TeleportItemRequirement getItemRequirement()
			{
				TeleportItemRequirement burthTele = new TeleportItemRequirement("Teleport to Burthorpe. Games necklace (Burthorpe [1]), minigame teleport (Burthorpe Games Room)",
					ItemCollections.GAMES_NECKLACES);
				return burthTele;
			}
		},
	VARROCK_TELEPORT()
		{
			public TeleportItemRequirement getItemRequirement()
			{
				TeleportItemRequirement varrockTele = new TeleportItemRequirement("Teleport to Varrock. Varrock teleport tablet/spell, Chronicle, Ring of Wealth (Grand Exchange [2])",
					ItemID.VARROCK_TELEPORT);
				varrockTele.addAlternates(ItemID.CHRONICLE);
				varrockTele.addAlternates(ItemCollections.RING_OF_WEALTHS);
				return varrockTele;
			}
		},
	SOPHANEM_TELEPORT()
		{
			public TeleportItemRequirement getItemRequirement()
			{
				TeleportItemRequirement sophTele = new TeleportItemRequirement("Teleport to Sophanem. Pharaoh's sceptre (Jalsavrah [1])",
					ItemCollections.PHAROAH_SCEPTRE);
				return sophTele;
			}
		};

	public abstract TeleportItemRequirement getItemRequirement();
}

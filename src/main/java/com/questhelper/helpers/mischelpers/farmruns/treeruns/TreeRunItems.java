package com.questhelper.helpers.mischelpers.farmruns.treeruns;

import com.questhelper.collections.ItemCollections;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import lombok.Getter;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;
import static com.questhelper.requirements.util.LogicHelper.not;

@Getter
public class TreeRunItems
{
	private final ItemRequirement coins;
	private final ItemRequirement spade;
	private final ItemRequirement rake;
	private final ItemRequirement compost;
	private final ItemRequirement axe;

	public TreeRunItems(TreeRunConfig config)
	{
		coins = new ItemRequirement("Coins to quickly remove trees.", ItemID.COINS)
			.showConditioned(config.getPayingForRemoval());
		spade = new ItemRequirement("Spade", ItemID.SPADE);
		rake = new ItemRequirement("Rake", ItemID.RAKE)
			.hideConditioned(new VarbitRequirement(VarbitID.FARMING_BLOCKWEEDS, 2));
		compost	= new ItemRequirement("Compost", ItemCollections.COMPOST).showConditioned(config.getUsingCompostOrNothing());
		compost.setDisplayMatchedItemName(true);
		axe = new ItemRequirement("Any axe", ItemCollections.AXES).isNotConsumed().showConditioned(not(config.getPayingForRemoval()));
	}
}

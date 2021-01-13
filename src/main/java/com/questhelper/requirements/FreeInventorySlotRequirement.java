package com.questhelper.requirements;

import java.util.Locale;
import java.util.stream.Stream;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;

@Getter
public class FreeInventorySlotRequirement extends Requirement
{
	private InventoryID inventoryID;
	private int numSlotsFree;

	public FreeInventorySlotRequirement(InventoryID inventoryID, int numSlotsFree) {
		this.inventoryID = inventoryID;
		this.numSlotsFree = numSlotsFree;
	}

	@Override
	public boolean check(Client client)
	{
		ItemContainer container = client.getItemContainer(getInventoryID());
		if (container != null) {
			return Stream.of(container.getItems()).filter(this::isOpenSlot).count() >= getNumSlotsFree();
		}
		return false;
	}

	private boolean isOpenSlot(Item item) {
		return item == null || item.getId() == -1;
	}

	@Override
	public String getDisplayText()
	{
		return getNumSlotsFree() + " free " + getInventoryID().name().toLowerCase(Locale.ROOT).replaceAll("_", " ") + " slots";
	}
}

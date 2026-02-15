package com.questhelper.requirements;

import com.questhelper.requirements.item.ItemRequirement;
import lombok.NonNull;
import net.runelite.client.game.ItemManager;
import javax.inject.Inject;

/**
 * Assists construction of {@link ItemRequirement}s.
 */
public class RequirementFactory {

	private final ItemManager itemManager;

	@Inject
	public RequirementFactory(@NonNull ItemManager itemManager) {
		this.itemManager = itemManager;
	}

	/**
	 * Create a new {@link ItemRequirement} with a game-provided name.
	 */
	public ItemRequirement newItem(int itemId) {
		String name = itemManager.getItemComposition(itemId).getMembersName();
		return new ItemRequirement(name, itemId);
	}
}

package com.questhelper.requirements.item;

import com.questhelper.MockedTest;
import com.questhelper.managers.QuestContainerManager;
import net.runelite.api.Item;
import net.runelite.api.Skill;
import net.runelite.api.gameval.ItemID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;

public class ItemRequirementTest extends MockedTest
{
	void initFishing(int realLevel, int boostedLevel)
	{
		when(client.getRealSkillLevel(Skill.FISHING)).thenReturn(realLevel);
		when(client.getBoostedSkillLevel(Skill.FISHING)).thenReturn(boostedLevel);
	}

	/// Ensures that the default-constructible ItemRequirement quantity is 1
	@Test
	void defaultQuantity()
	{
		var a = new ItemRequirement("Rope", ItemID.ROPE);
		var b = new ItemRequirement("Rope", ItemID.ROPE, 1);

		assertEquals(a.quantity, b.quantity);
		assertEquals(a.getQuantity(), b.getQuantity());
	}

	/// Ensures the quantity indicator in the display text is applied correctly
	@Test
	void getDisplayText()
	{
		var one = new ItemRequirement("Rope", ItemID.ROPE, 1);
		var many = new ItemRequirement("Rope", ItemID.ROPE, 5);
		var none = new ItemRequirement("Rope", ItemID.ROPE, -1);

		assertEquals("1 x Rope", one.getDisplayText());
		assertEquals("5 x Rope", many.getDisplayText());
		assertEquals("Rope", none.getDisplayText());
	}

	// TODO: Test conditionToHide

	/// This test sets up a scenario where the player does _not_ have the given item in any container
	@Test
	void checkFail()
	{
		setupContainers(
			// Inventory
			new Item[]{},

			// Equipment
			new Item[]{},

			// Bank
			new Item[]{}
		);

		var normal = new ItemRequirement("Rope", ItemID.ROPE);

		assertFalse(normal.check(client));
		assertEquals(questHelperConfig.failColour(), normal.getColor(client, questHelperConfig));
	}

	private void setupContainers(Item[] inventory, Item[] equipment, Item[] bank)
	{
		QuestContainerManager.getInventoryData().update(-1, inventory);
		QuestContainerManager.getEquippedData().update(-1, equipment);
		QuestContainerManager.getBankData().update(-1, bank);
	}

	/// This test sets up a scenario where the player has the item in their bank
	@Test
	void checkInBank()
	{
		setupContainers(
			// Inventory
			new Item[]{},

			// Equipment
			new Item[]{},

			// Bank
			new Item[]{
				new Item(ItemID.ROPE, 5),
			}
		);

		var normal = new ItemRequirement("Rope", ItemID.ROPE);

		// Partial success because the overlay will state: "you don't have it, BUT, it's in the bank!"
		assertFalse(normal.check(client));
		assertEquals(questHelperConfig.partialSuccessColour(), normal.getColor(client, questHelperConfig));

		var withBank = normal.copy();
		withBank.setShouldCheckBank(true);

		// setShouldCheckBank does not actually make the requirement green, it just means it should pass any requirement/condition checks
		// that are used for conditional logic, such as in ConditionalStep.addStep(withBank, ...)
		assertTrue(withBank.check(client));
		assertEquals(questHelperConfig.partialSuccessColour(), withBank.getColor(client, questHelperConfig));
	}

	/// This test sets up a scenario where the player has the item in their inventory
	@Test
	void checkInInventory()
	{
		setupContainers(
			// Inventory
			new Item[]{
				new Item(ItemID.ROPE, 1),
			},

			// Equipment
			new Item[]{},

			// Bank
			new Item[]{}
		);

		var normal = new ItemRequirement("Rope", ItemID.ROPE);

		assertTrue(normal.check(client));
		assertEquals(questHelperConfig.passColour(), normal.getColor(client, questHelperConfig));
	}

	/// This test sets up a scenario where the player has the item equipped
	/// This should result in the same behaviour as if the user had it in their inventory
	@Test
	void checkInEquipment()
	{
		setupContainers(
			// Inventory
			new Item[]{},

			// Equipment
			new Item[]{
				new Item(ItemID.ROPE, 1),
			},

			// Bank
			new Item[]{}
		);

		var normal = new ItemRequirement("Rope", ItemID.ROPE);

		assertTrue(normal.check(client));
		assertEquals(questHelperConfig.passColour(), normal.getColor(client, questHelperConfig));
	}

	/// This test sets up a scenario where the player has the item in the inventory, but it must be equipped
	@Test
	void checkInInventoryMustBeEquipped()
	{
		setupContainers(
			// Inventory
			new Item[]{
				new Item(ItemID.ROPE, 1),
			},

			// Equipment
			new Item[]{
			},

			// Bank
			new Item[]{}
		);

		var normal = new ItemRequirement("Rope", ItemID.ROPE);
		normal.setMustBeEquipped(true);

		// The item must be equipped, but it's only in the inventory, so it should fail
		assertFalse(normal.check(client));

		// The item must be equipped, but it's only in the inventory. getColor still returns the pass color.
		// We append (equipped) next to the name that's red to signify tht it's not fully passed, but can be by the user clicking equip on the item.
		assertEquals(questHelperConfig.passColour(), normal.getColor(client, questHelperConfig));
	}
}

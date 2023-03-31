package com.questhelper.steps.playermadesteps.extendedruneliteobjects;

import java.awt.Shape;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.callback.ClientThread;

public class ReplacedNpc extends FakeNpc
{
	@Getter
	@Setter
	private NPC npc;

	@Getter
	@Setter
	private int npcIDToReplace;

	@Getter
	private final ArrayList<MenuEntryWrapper> entries = new ArrayList<>();

	protected ReplacedNpc(Client client, ClientThread clientThread, WorldPoint worldPoint, int[] model, int animation, int npcIDToReplace)
	{
		super(client, clientThread, worldPoint, model, animation);
		this.npcIDToReplace = npcIDToReplace;
		disable();
	}

	public void updateNpcSync(Client client)
	{
		getRuneliteObject().setLocation(npc.getLocalLocation(), client.getPlane());
		setAnimation(npc.getAnimation());
		getRuneliteObject().setOrientation(npc.getOrientation());
		getRuneliteObject().setActive(true);
	}

	public void addMenuEntry(MenuEntryWrapper menuEntry)
	{
		entries.add(menuEntry);
	}

	public Shape getClickbox()
	{
		return Perspective.getClickbox(client, getRuneliteObject().getModel(), getRuneliteObject().getOrientation(), getRuneliteObject().getLocation().getX(), getRuneliteObject().getLocation().getY(),
			Perspective.getTileHeight(client, getRuneliteObject().getLocation(), getWorldPoint().getPlane()));
	}
}

package com.questhelper.steps.playermadesteps.extendedruneliteobjects;

import java.awt.Shape;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
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

	protected ReplacedNpc(Client client, ClientThread clientThread, WorldPoint worldPoint, int[] model, int npcIDToReplace)
	{
		super(client, clientThread, worldPoint, model, 808);
		this.npcIDToReplace = npcIDToReplace;
//		disable();
	}

	public void updateNpcSync(Client client)
	{
		if (npc.getAnimation() != -1)
		{
			setAnimation(npc.getAnimation());
		}
		else if (npc.getLocalLocation().distanceTo(getRuneliteObject().getLocation()) == 0)
		{
				setAnimation(npc.getIdlePoseAnimation());
		}
		else
		{
			setAnimation(npc.getWalkAnimation());
		}
		getRuneliteObject().setLocation(npc.getLocalLocation(), client.getPlane());
		setOrientationGoal(npc.getOrientation());
		if (!isActive())
		{
			activate();
		}
	}

	public void addMenuEntry(MenuEntryWrapper menuEntry)
	{
		entries.add(menuEntry);
	}

	// This changes the clickbox to be the original NPC's clickbox to avoid any possible advantage is interacting
	public Shape getClickbox()
	{
		return Perspective.getClickbox(client, npc.getModel(), npc.getOrientation(), npc.getLocalLocation().getX(), npc.getLocalLocation().getY(),
			Perspective.getTileHeight(client, npc.getLocalLocation(), getWorldPoint().getPlane()));
	}
}

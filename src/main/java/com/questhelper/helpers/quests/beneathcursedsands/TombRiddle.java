package com.questhelper.helpers.quests.beneathcursedsands;

import com.google.inject.Inject;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

import java.util.*;

public class TombRiddle extends DetailedOwnerStep
{
	@Inject
	protected EventBus eventBus;

	@Inject
	protected Client client;

	Integer northernEmblem, centreNorthEmblem, centreSouthEmblem, southernEmblem;

	private final HashMap<String, Integer> gods = new HashMap<>();
	private final HashMap<String, Integer> items = new HashMap<>();

	private final HashMap<Integer, ItemRequirement> emblems = new HashMap<>();

	private boolean solutionFound;

	ItemRequirement baboonEmblem, humanEmblem, crocodileEmblem, scarabEmblem;

	DetailedQuestStep inspectPlaque, obtainEmblems, placeNorthernUrn, placeCentreNorthUrn, placeCentreSouthUrn, placeSouthernUrn, pullLever;

	public TombRiddle(QuestHelper questHelper)
	{
		super(questHelper, "Solve the riddle.");

		final int SCARAB = 4;
		final int HUMAN = 2;
		final int CROCODILE = 3;
		final int BABOON = 1;

		gods.put("god of isolation", SCARAB);
		gods.put("god of health", HUMAN);
		gods.put("goddess of resourcefulness", CROCODILE);
		gods.put("goddess of companionship", BABOON);

		items.put("a carving", SCARAB);
		items.put("some wine", HUMAN);
		items.put("a necklace", CROCODILE);
		items.put("some linen", BABOON);

		emblems.put(BABOON, baboonEmblem);
		emblems.put(HUMAN, humanEmblem);
		emblems.put(CROCODILE, crocodileEmblem);
		emblems.put(SCARAB, scarabEmblem);
	}

	@Override
	public void startUp()
	{
		updateSteps();
	}

	@Override
	public void shutDown()
	{
		shutDownStep();
		currentStep = null;
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		updateSteps();
	}

	protected void updateSteps()
	{
		if (!solutionFound)
		{
			startUpStep(inspectPlaque);
			return;
		}

		int currentNorthernEmblem = client.getVarbitValue(VarbitID.BCS_URN_4);
		int currentCentreNorthEmblem = client.getVarbitValue(VarbitID.BCS_URN_3);
		int currentCentreSouthEmblem = client.getVarbitValue(VarbitID.BCS_URN_2);
		int currentSouthernEmblem = client.getVarbitValue(VarbitID.BCS_URN_1);

		if (currentNorthernEmblem != northernEmblem)
		{
			if (!emblems.get(northernEmblem).check(client)) {
				startUpStep(obtainEmblems);
			}
			else
			{
				startUpStep(placeNorthernUrn);
			}
		}
		else if (currentCentreNorthEmblem != centreNorthEmblem)
		{
			if (!emblems.get(centreNorthEmblem).check(client))
			{
				startUpStep(obtainEmblems);
			}
			else
			{
				startUpStep(placeCentreNorthUrn);
			}
		}
		else if (currentCentreSouthEmblem != centreSouthEmblem)
		{
			if (!emblems.get(centreSouthEmblem).check(client))
			{
				startUpStep(obtainEmblems);
			}
			else
			{
				startUpStep(placeCentreSouthUrn);
			}
		}
		else if (currentSouthernEmblem != southernEmblem)
		{
			if (!emblems.get(southernEmblem).check(client))
			{
				startUpStep(obtainEmblems);
			}
			else
			{
				startUpStep(placeSouthernUrn);
			}
		}
		else
		{
			startUpStep(pullLever);
		}
	}

	private void setupItemRequirements()
	{
		baboonEmblem = new ItemRequirement("Baboon emblem", ItemID.BCS_RIDDLE_EMBLEM_BABOON);
		baboonEmblem.setHighlightInInventory(true);
		humanEmblem = new ItemRequirement("Human emblem", ItemID.BCS_RIDDLE_EMBLEM_HUMAN);
		humanEmblem.setHighlightInInventory(true);
		crocodileEmblem = new ItemRequirement("Crocodile emblem", ItemID.BCS_RIDDLE_EMBLEM_CROCODILE);
		crocodileEmblem.setHighlightInInventory(true);
		scarabEmblem = new ItemRequirement("Scarab emblem", ItemID.BCS_RIDDLE_EMBLEM_SCARAB);
		scarabEmblem.setHighlightInInventory(true);
	}

	@Override
	protected void setupSteps()
	{
		inspectPlaque = new ObjectStep(questHelper, ObjectID.BCS_RIDDLE_PLAQUE, new WorldPoint(3391, 9251, 0), "Inspect the north-western plaque and read it.");
		obtainEmblems = new ObjectStep(questHelper, ObjectID.BCS_EMBLEM_PLAQUE, new WorldPoint(3391, 9245, 0), "Inspect the south-western plaque to get four emblems.");
		pullLever = new ObjectStep(questHelper, ObjectID.BCS_TOMB_WALL_LEVER, new WorldPoint(3390, 9247, 0), "Pull the lever to the south-west.");

		placeNorthernUrn = new ObjectStep(questHelper, ObjectID.BCS_TOMB_URN_4, "Place the emblem in the northernmost urn.");
		placeCentreNorthUrn = new ObjectStep(questHelper, ObjectID.BCS_TOMB_URN_3, "Place the emblem in the centre-north urn.");
		placeCentreSouthUrn = new ObjectStep(questHelper, ObjectID.BCS_TOMB_URN_2, "Place the emblem in the centre-south urn.");
		placeSouthernUrn = new ObjectStep(questHelper, ObjectID.BCS_TOMB_URN_1, "Place the emblem in the southernmost urn.");

		setupItemRequirements();
	}

	@Override
	public Collection<QuestStep> getSteps()
	{
		return Arrays.asList(inspectPlaque, obtainEmblems, placeNorthernUrn, placeCentreNorthUrn, placeCentreSouthUrn, placeSouthernUrn, pullLever);
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded widgetLoaded)
	{
		// The instructions are contained in the Text fields of Widget 749.2's children (IDs 749.3 through 749.13)
		if (!solutionFound && widgetLoaded.getGroupId() == 749)
		{
			Widget plaqueWidget = client.getWidget(InterfaceID.Woodplaque.CONTENT);
			if (plaqueWidget == null || plaqueWidget.getStaticChildren() == null)
			{
				return;
			}

			String riddle = Arrays.stream(plaqueWidget.getStaticChildren())
				.map(Widget::getText)
				.reduce("", (carry, widget) -> carry + widget + " ");

			if (riddle.length() > 0)
			{
				northernEmblem = gods.entrySet().stream()
					.filter(e -> riddle.contains(e.getKey() + " arrived just before"))
					.map(Map.Entry::getValue).findFirst()
					.orElse(null);

				centreNorthEmblem = gods.entrySet().stream()
					.filter(e -> riddle.contains("arrived just before the " + e.getKey()))
					.map(Map.Entry::getValue).findFirst()
					.orElse(null);

				centreSouthEmblem = items.entrySet().stream()
					.filter(e -> riddle.contains("To the one that arrived first, he offered " + e.getKey()))
					.map(Map.Entry::getValue).findFirst()
					.orElse(null);

				southernEmblem = items.entrySet().stream()
					.filter(e -> riddle.contains("The one that was offered " + e.getKey()))
					.map(Map.Entry::getValue).findFirst()
					.orElse(null);

				if (northernEmblem == null || centreNorthEmblem == null || centreSouthEmblem == null || southernEmblem == null)
				{
					return;
				}

				placeNorthernUrn.addItemRequirements(Collections.singletonList(emblems.get(northernEmblem)));
				placeNorthernUrn.addIcon(emblems.get(northernEmblem).getId());
				placeCentreNorthUrn.addItemRequirements(Collections.singletonList(emblems.get(centreNorthEmblem)));
				placeCentreNorthUrn.addIcon(emblems.get(centreNorthEmblem).getId());
				placeCentreSouthUrn.addItemRequirements(Collections.singletonList(emblems.get(centreSouthEmblem)));
				placeCentreSouthUrn.addIcon(emblems.get(centreSouthEmblem).getId());
				placeSouthernUrn.addItemRequirements(Collections.singletonList(emblems.get(southernEmblem)));
				placeSouthernUrn.addIcon(emblems.get(southernEmblem).getId());

				solutionFound = true;
			}
		}
	}
}

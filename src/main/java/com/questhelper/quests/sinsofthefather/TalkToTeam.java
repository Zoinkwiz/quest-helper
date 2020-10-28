//package net.runelite.client.plugins.questhelper.quests.sinsofthefather;
//
//import net.runelite.api.coords.WorldPoint;
//import net.runelite.api.widgets.Widget;
//import net.runelite.api.widgets.WidgetInfo;
//import net.runelite.client.plugins.questhelper.questhelpers.QuestHelper;
//import net.runelite.client.plugins.questhelper.steps.*;
//
//public class SpeakToTeamStep extends MultiStageStep {
//	private boolean spokenToIvan,
//		spokenToVertida,
//		spokenToRadigad,
//		spokenToPolmafi,
//		spokenToKael,
//		spokenToVeliaf;
//
//
//	private enum Steps {
//		UNKNOWN(-1),
//		SPEAK_TO_IVAN(0),
//		SPEAK_TO_VERTIDA(1),
//		SPEAK_TO_RADIGAD(2),
//		SPEAK_TO_POLMAFI(3),
//		SPEAK_TO_KAEL(4),
//		SPEAK_TO_VELIAF(5);
//
//		private final int value;
//
//		Steps(int value) {
//			this.value = value;
//		}
//
//		public int getValue() {
//			return value;
//		}
//	}
//
//	public SpeakToTeamStep(QuestHelper questHelper) {
//		super(questHelper, null);
//		loadSteps();
//	}
//
//	@Override
//	public void startUp()
//	{
//		updateSteps();
//	}
//
//	protected void updateSteps() {
//		if (!knownState) {
//			startUpStep(steps.get(Steps.UNKNOWN.getValue()));
//		} else {
//			if(!spokenToIvan) {
//				startUpStep(steps.get(Steps.SPEAK_TO_IVAN.getValue()));
//			} else if(!spokenToVertida) {
//				startUpStep(steps.get(Steps.SPEAK_TO_VERTIDA.getValue()));
//			} else if(!spokenToRadigad) {
//				startUpStep(steps.get(Steps.SPEAK_TO_RADIGAD.getValue()));
//			} else if(!spokenToKael) {
//				startUpStep(steps.get(Steps.SPEAK_TO_KAEL.getValue()));
//			} else if(!spokenToPolmafi) {
//				startUpStep(steps.get(Steps.SPEAK_TO_POLMAFI.getValue()));
//			} else {
//				startUpStep(steps.get(Steps.SPEAK_TO_VELIAF.getValue()));
//			}
//		}
//	}
//
//	public void gameTick() {
//		Widget widgetTitle = client.getWidget(WidgetInfo.DIARY_QUEST_WIDGET_TITLE);
//		String lineThrough = "<str>";
//
//		if (!knownState && widgetTitle != null && widgetTitle.getText().contains(getQuestHelper().getQuest().getName())) {
//			spokenToKael = client.getWidget(119, 92).getText().contains(lineThrough);
//			spokenToVertida = client.getWidget(119, 93).getText().contains(lineThrough);
//			spokenToPolmafi = client.getWidget(119, 94).getText().contains(lineThrough);
//			spokenToRadigad = client.getWidget(119, 95).getText().contains(lineThrough);
//			spokenToIvan = client.getWidget(119, 96).getText().contains(lineThrough);
//			spokenToVeliaf = client.getWidget(119, 97).getText().contains(lineThrough);
//			if (spokenToVertida || spokenToIvan || spokenToKael || spokenToPolmafi || spokenToRadigad) {
//				knownState = true;
//				updateSteps();
//			}
//		}
//
//		checkDialogue();
//	}
//
//	private void checkDialogue() {
//		Widget dialogNpc = client.getWidget(WidgetInfo.DIALOG_NPC_TEXT);
//		Widget dialogPlayer = client.getWidget(WidgetInfo.DIALOG_PLAYER_TEXT);
//		if (dialogNpc != null) {
//			if(dialogNpc.getText().equals("Of course! Let's rid Morytania of evil, once and for all!")) {
//				spokenToRadigad = true;
//				updateSteps();
//			}
//		}
//
//		if(dialogPlayer != null) {
//			if(dialogPlayer.getText().equals("Alright, thanks Vertida.")) {
//				spokenToVertida = true;
//				updateSteps();
//			} else if(dialogPlayer.getText().equals("He'll regret the day he crossed the Myreque.")) {
//				spokenToKael = true;
//				updateSteps();
//			} else if(dialogPlayer.getText().equals("Great, thanks.")) {
//				spokenToPolmafi = true;
//				updateSteps();
//			} else if(dialogPlayer.getText().equals("I guess I'd better talk to him then.")) {
//				spokenToIvan = true;
//				updateSteps();
//			}
//		}
//	}
//
//	@Override
//	protected void loadSteps()
//	{
//		steps.put(Steps.UNKNOWN.getValue(), new ResyncStep(getQuestHelper()));
//
//		steps.put(Steps.SPEAK_TO_IVAN.getValue(), new NpcTalkStep(getQuestHelper(), 9530, new WorldPoint(3696, 3182, 0),
//			"Speak to Ivan Strom in the Icyene Graveyard."));
//
//		steps.put(Steps.SPEAK_TO_VERTIDA.getValue(), new NpcTalkStep(getQuestHelper(), 9547, new WorldPoint(3694, 3185, 0),
//			"Speak to Verdita Sefalatis in the Icyene Graveyard."));
//
//		steps.put(Steps.SPEAK_TO_RADIGAD.getValue(), new NpcTalkStep(getQuestHelper(), 9551, new WorldPoint(3689, 3188, 0),
//			"Speak to Radigad Ponfit in the Icyene Graveyard."));
//
//		steps.put(Steps.SPEAK_TO_KAEL.getValue(), new NpcTalkStep(getQuestHelper(), 9543, new WorldPoint(3685, 3184, 0),
//			"Speak to Kael Forshaw in the Icyene Graveyard."));
//
//		steps.put(Steps.SPEAK_TO_POLMAFI.getValue(), new NpcTalkStep(getQuestHelper(), 9554, new WorldPoint(3704, 3182, 0),
//			"Speak to Polmafi Ferdygris in the Icyene Graveyard."));
//
//		steps.put(Steps.SPEAK_TO_VELIAF.getValue(), new NpcTalkStep(getQuestHelper(), 9489, new WorldPoint(3690, 3183, 0),
//			"Speak to Veliaf Hurtz in the Icyene Graveyard."));
//
//	}
//
//}

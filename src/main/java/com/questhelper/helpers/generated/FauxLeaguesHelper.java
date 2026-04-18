package com.questhelper.helpers.generated;

import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.ChatMessageRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.ObjectCondition;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.conditional.Conditions;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.not;
import static com.questhelper.requirements.util.LogicHelper.or;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.ManualRequirement;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.EmoteStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.List;
import com.questhelper.steps.emote.QuestEmote;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.Skill;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.SpriteID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;

public class FauxLeaguesHelper extends ComplexStateQuestHelper
{

	// Captured requirements
	ItemRequirement uncutRuby;
	ItemRequirement bigFishingNet;
	ItemRequirement bones;
	ItemRequirement dramenStaff;
	ItemRequirement knife;
	ItemRequirement coins;
	ItemRequirement brownApron;
	ItemRequirement redCape;
	ItemRequirement bronzeAxe;
	ItemRequirement ironMace;
	ItemRequirement logs;
	ItemRequirement ruby;
	ItemRequirement emerald;
	ItemRequirement sapphire;
	ItemRequirement needle;
	ItemRequirement thread;
	ItemRequirement wool;
	ItemRequirement cowhide;
	ItemRequirement leather;
	ItemRequirement chisel;
	ItemRequirement flax;
	ItemRequirement cake;
	ItemRequirement bucket;
	ItemRequirement bucketOfWater;
	ItemRequirement pot;
	ItemRequirement grain;
	ItemRequirement jugOfWine;
	ItemRequirement stew;
	ItemRequirement rawRabbit;
	ItemRequirement cookedRabbit;
	ItemRequirement moonLite;
	ItemRequirement grapeBarrel;
	ItemRequirement rake;
	ItemRequirement orangeDye;
	ItemRequirement blueDye;
	ItemRequirement yellowDye;
	ItemRequirement redDye;
	ItemRequirement purpleDye;
	ItemRequirement shortbow;
	ItemRequirement smallFishingNet;
	ItemRequirement rawSardine;
	ItemRequirement fishingRod;
	ItemRequirement fishingBait;
	ItemRequirement rawShrimps;
	ItemRequirement ironArrow;
	ItemRequirement fiendishAshes;
	ItemRequirement spade;
	ItemRequirement birdSnare;
	ItemRequirement unlitTorch;
	ItemRequirement tinderbox;
	ItemRequirement tyrasHelm;
	ItemRequirement litTorch;
	ItemRequirement bucketOfSand;
	ItemRequirement pineapple;
	ItemRequirement cupOfTea;
	ItemRequirement staffOfAir;
	ItemRequirement staffOfFire;
	ItemRequirement forestryKit;
	ItemRequirement harpoon;
	ItemRequirement lobsterPot;
	ItemRequirement flyFishingRod;
	ItemRequirement eyeOfNewtPack;
	ItemRequirement waterFilledVialPack;
	ItemRequirement pestleAndMortar;
	ItemRequirement adamantPlatebody;
	ItemRequirement adamantPlatelegs;
	ItemRequirement bronzePlatelegs;
	ItemRequirement bronzePlatebody;
	ItemRequirement mithrilSpear;
	ItemRequirement steelPlatebody;
	ItemRequirement ironDagger;
	ItemRequirement bucketOfMilk;
	ItemRequirement patOfButter;
	ItemRequirement potato;
	ItemRequirement bakedPotato;
	ItemRequirement potatoWithButter;
	ItemRequirement potatoSeed;
	ItemRequirement seedDibber;
	ItemRequirement compost;
	ItemRequirement softClay;
	ItemRequirement bronzePickaxe;
	ItemRequirement pinkSkirt;
	ItemRequirement copperOre;
	ItemRequirement shears;
	ItemRequirement glassblowingPipe;
	ItemRequirement moltenGlass;
	ItemRequirement sodaAsh;
	ItemRequirement feathers;
	ItemRequirement steelAxe;

	// Captured steps
	DetailedQuestStep openleaguesmenu;
	DetailedQuestStep completeleaguestutorial;
	DetailedQuestStep togglerunenergy;
	NpcStep pickpocketcitizen;
	NpcStep petrenu;
	DetailedQuestStep bownearrenu;
	ObjectStep usewaterpump;
	NpcStep talktoportmaster;
	ObjectStep zanarisfairyring;
	EmoteStep crynearachild;
	ObjectStep stealcakesfrombakersstall;
	NpcStep tradeshopkeeper;
	NpcStep attackratlevel1;
	DetailedQuestStep salutequoatlos;
	ObjectStep useprayernearshrineofralos;
	NpcStep petxolo;
	ObjectStep drinkfrombirdbath;
	ObjectStep chopdowntree;
	NpcStep tradefloria;
	DetailedQuestStep burnlog;
	DetailedQuestStep fletchlog;
	ObjectStep attackdummy;
	NpcStep useeastcivitasbank;
	ConditionalStep talktogladiator;
	NpcStep talktotradercrewmember;
	NpcStep talktotradercrewmember2;
	ObjectStep usefurnace;
	ObjectStep usefurnace2;
	DetailedQuestStep blowglassintooillanterns;
	NpcStep depositmoltenglass;
	NpcStep attackchickenlevel1;
	ObjectStep picksweetcorn;
	ObjectStep pickandeatonion;
	ObjectStep pickwheat;
	ConditionalStep emptyflourbin;
	ObjectStep pickpotato;
	NpcStep tradeharminia;
	ObjectStep rakeflowerpatch;
	ObjectStep milkdairybuffalo;
	ObjectStep milkdairybuffalo2;
	NpcStep shearalpaca;
	NpcStep tradeartima;
	NpcStep travelwithrenu;
	DetailedQuestStep hometeleport;
	DetailedQuestStep returntocivitas;
	NpcStep feedbonestomolossus;
	NpcStep talktovineyardforeman;
	NpcStep returntovineyardforeman;
	NpcStep withdrawaldarinbanker;
	NpcStep withdrawaldarinbanker2;
	NpcStep tradetoci;
	DetailedQuestStep cutextragems;
	NpcStep travelbackwithrenu;
	DetailedQuestStep claimfirstrelic;
	DetailedQuestStep equipapronandredcape;
	NpcStep useeastcivitasbankagain;
	NpcStep depositoillanterns;
	ObjectStep pickcabbage;
	DetailedQuestStep emptywaterbucket;
	NpcStep attackcowlevel2;
	DetailedQuestStep burybones;
	ObjectStep aldarinfairyring;
	DetailedQuestStep collectshimmeringgrapes;
	NpcStep attackseagulllevel2;
	NpcStep tradechartership;
	ObjectStep cookrabbitonoven;
	DetailedQuestStep eatcookedrabbit;
	NpcStep talktoantonia;
	NpcStep talktoantonia2;
	ObjectStep fillabucketwithsand;
	NpcStep talktopicaria;
	NpcStep chartertocivitas;
	NpcStep castfishingspot;
	NpcStep talktobartender;
	DetailedQuestStep drinkmoonlite;
	DetailedQuestStep drinkjugofwine;
	NpcStep pickpocketcitizenuntilsuccess;
	NpcStep tradesilkmerchant;
	NpcStep givestewtooli;
	NpcStep buypoh;
	NpcStep buyironmace;
	NpcStep traveltoauburnvale;
	NpcStep tradesebamo;
	NpcStep tradelunami;
	NpcStep depositinauburnvale;
	DetailedQuestStep equipironmace;
	DetailedQuestStep equipironmace2;
	ObjectStep chopdowndeadtree;
	NpcStep makeplankatsawmill;
	NpcStep tradeauburnvaleshopkeeper;
	NpcStep tanleatheratchouani;
	DetailedQuestStep craftleatherchaps;
	ObjectStep admirebeautifullog;
	ObjectStep steponenttrail;
	ObjectStep pickflax;
	ObjectStep spinflaxandwool;
	NpcStep traveltotalteklan;
	NpcStep tradeteicuh;
	NpcStep tradexochitl;
	DetailedQuestStep dyecape;
	DetailedQuestStep dancenearabard;
	NpcStep tradearcuani;
	NpcStep attackfroglevel5;
	ObjectStep chopdowndeadtreerainforest;
	NpcStep traveltokastori;
	NpcStep tradesulisal;
	NpcStep depositintalteklan;
	NpcStep sardinefishingspot;
	ObjectStep cooksardinesatoven;
	NpcStep shrimpfishingspot;
	ObjectStep cookshrimpatoven;
	NpcStep herringfishingspot;
	NpcStep anchovyfishingspot;
	ObjectStep climbdownladder;
	NpcStep attackimplevel2;
	ObjectStep climbupladder;
	NpcStep petcaique;
	ObjectStep activatestatuekastori;
	NpcStep attackgemstonecrablevel160;
	DetailedQuestStep witnessthegemcrabsdefeat;
	NpcStep travelgloomthorn;
	ObjectStep activatestatuenemus;
	NpcStep attackicefiendlevel13;
	NpcStep attackrabbitlevel2;
	ObjectStep activatestatuedarkfrost;
	DetailedQuestStep yamaagility;
	DetailedQuestStep scatterfiendishashes;
	NpcStep travelaldarin;
	DetailedQuestStep sitwithdogs;
	NpcStep buytorch;
	DetailedQuestStep lighttorch;
	NpcStep buypineapples;
	NpcStep depositpineapples;
	DetailedQuestStep drinkcupoftea;
	NpcStep talktofriendlyforester;
	DetailedQuestStep lowalchrunes;
	NpcStep withdrawtyrashelm;
	ObjectStep enterportal;
	DetailedQuestStep buildroom;
	ObjectStep exitportal;
	NpcStep tradetociagain;
	NpcStep tradethurid;
	NpcStep buyfishfrompicaria;
	ObjectStep entercamtorum;
	NpcStep tradehuito;
	NpcStep tradetizoro;
	NpcStep haircutsonalo;
	NpcStep shavesonalo;
	NpcStep tradenahta;
	NpcStep depositcamtorumbanker;
	NpcStep tradecamtorumbartender;
	DetailedQuestStep leaguemenuteleport;
	NpcStep tradefortisblacksmith;
	NpcStep tradecamtorumblacksmith;
	DetailedQuestStep equipmithrilspear;
	DetailedQuestStep lowalchsteelplatebody;
	NpcStep depositfortiswestbank;
	ObjectStep enterguardbunker;
	NpcStep attackguardlevel21;
	ObjectStep exitguardbunker;
	ObjectStep enterthiefsden;
	NpcStep attackthieflevel16;
	ObjectStep exitthiefsden;
	NpcStep attackbuffalolevel9;
	ObjectStep churndairychurn;
	ObjectStep pickpotato2;
	ObjectStep cookfarmingpatchoven;
	DetailedQuestStep combinebutterwithpotato;
	ObjectStep fillcompostbin;
	NpcStep pickpocketmasterfarmer;
	ObjectStep rakeallotment;
	ObjectStep plantpotatoseeds;
	NpcStep buyseeddibberharminia;
	NpcStep payprotectharminia;
	NpcStep attackdirewolflevel88;
	NpcStep attackscorpionlevel14;
	ObjectStep minecopperrocks;
	ObjectStep minetinrocks;
	NpcStep attackjaguarlevel67;
	NpcStep attackhillgiantlevel28;
	ObjectStep mineclayrocks;
	ObjectStep mineironrocks;
	ObjectStep minesilverrocks;
	ObjectStep minecoalrocks;
	ObjectStep minegoldrocks;
	ObjectStep minemithrilrocks;
	ObjectStep minemoreiron;
	NpcStep bignetcoastfishingspot;
	NpcStep bignetcoastfishingspotcod;
	NpcStep attackoryxlevel15;
	NpcStep buymouldsartima;
	ObjectStep softclayspawn;
	ObjectStep usepotterswheel;
	ObjectStep firepotteryoven;
	DetailedQuestStep thankfaux;
	DetailedQuestStep defeatajaguar;
	DetailedQuestStep defeatajaguar2;
	ConditionalStep section1Task;
	ConditionalStep section2Task;
	ConditionalStep section3Task;
	ConditionalStep section4Task;
	ConditionalStep section5Task;
	ConditionalStep section6Task;
	ConditionalStep section7Task;
	ConditionalStep section8Task;
	ConditionalStep section9Task;
	ConditionalStep section10Task;

	@Override
	protected void setupRequirements()
	{
		uncutRuby = new ItemRequirement("Uncut Ruby", 0);
		bigFishingNet = new ItemRequirement("Big fishing net", 305);
		bones = new ItemRequirement("Bones", 526);
		dramenStaff = new ItemRequirement("Dramen Staff", 772);
		knife = new ItemRequirement("Knife", 946);
		coins = new ItemRequirement("Coins", 995);
		brownApron = new ItemRequirement("Brown apron", 1757);
		redCape = new ItemRequirement("red cape", ItemID.RED_CAPE);
		bronzeAxe = new ItemRequirement("Bronze axe", 1351);
		ironMace = new ItemRequirement("Iron mace", 1420);
		logs = new ItemRequirement("Logs", 1511);
		ruby = new ItemRequirement("Ruby", 1603);
		emerald = new ItemRequirement("Emerald", 1605);
		sapphire = new ItemRequirement("Sapphire", 1607);
		needle = new ItemRequirement("Needle", 1733);
		thread = new ItemRequirement("Thread", 1734);
		wool = new ItemRequirement("Wool", 1737);
		cowhide = new ItemRequirement("Cowhide", 1739);
		leather = new ItemRequirement("Leather", 1741);
		chisel = new ItemRequirement("Chisel", 1755);
		shears = new ItemRequirement("Shears", ItemID.SHEARS);
		glassblowingPipe = new ItemRequirement("Glassblowing pipe", ItemID.GLASSBLOWINGPIPE);
		moltenGlass = new ItemRequirement("Molten glass", ItemID.MOLTEN_GLASS);
		sodaAsh = new ItemRequirement("Soda ash", ItemID.SODA_ASH);
		flax = new ItemRequirement("Flax", 1779);
		cake = new ItemRequirement("Cake", 1891);
		bucket = new ItemRequirement("Bucket", 1925);
		bucketOfWater = new ItemRequirement("Bucket of water", 1929);
		pot = new ItemRequirement("Pot", 1931);
		grain = new ItemRequirement("Grain", 1947);
		jugOfWine = new ItemRequirement("Jug of wine", 1993);
		stew = new ItemRequirement("Stew", 2003);
		rawRabbit = new ItemRequirement("Raw rabbit", 3226);
		cookedRabbit = new ItemRequirement("Cooked rabbit", 3228);
		moonLite = new ItemRequirement("Moon-lite", 29418);
		grapeBarrel = new ItemRequirement("Grape barrel", 30037);
		rake = new ItemRequirement("Rake", 5341);
		orangeDye = new ItemRequirement("Orange dye", 1769);
		blueDye = new ItemRequirement("Blue dye", 1767);
		yellowDye = new ItemRequirement("Yellow dye", 1765);
		redDye = new ItemRequirement("Red dye", 1763);
		purpleDye = new ItemRequirement("Purple dye", 1773);
		shortbow = new ItemRequirement("Shortbow", 841);
		smallFishingNet = new ItemRequirement("Small fishing net", 303);
		rawSardine = new ItemRequirement("Raw sardine", 327);
		fishingRod = new ItemRequirement("Fishing rod", 307);
		fishingBait = new ItemRequirement("Fishing bait", 313);
		rawShrimps = new ItemRequirement("Raw shrimps", 317);
		ironArrow = new ItemRequirement("Iron arrow", 884);
		fiendishAshes = new ItemRequirement("Fiendish ashes", 25766);
		spade = new ItemRequirement("Spade", 952);
		birdSnare = new ItemRequirement("Bird snare", 10006);
		unlitTorch = new ItemRequirement("Unlit torch", 596);
		tinderbox = new ItemRequirement("Tinderbox", 590);
		tyrasHelm = new ItemRequirement("Tyras helm", 9629);
		litTorch = new ItemRequirement("Lit torch", 594);
		bucketOfSand = new ItemRequirement("Bucket of sand", 1783);
		pineapple = new ItemRequirement("Pineapple", 2114);
		cupOfTea = new ItemRequirement("Cup of tea", 1978);
		staffOfAir = new ItemRequirement("Staff of air", 1381);
		staffOfFire = new ItemRequirement("Staff of fire", 1387);
		forestryKit = new ItemRequirement("Forestry kit", 28136);
		harpoon = new ItemRequirement("Harpoon", 311);
		lobsterPot = new ItemRequirement("Lobster pot", 301);
		flyFishingRod = new ItemRequirement("Fly fishing rod", 309);
		eyeOfNewtPack = new ItemRequirement("Eye of newt pack", 12859);
		waterFilledVialPack = new ItemRequirement("Water-filled vial pack", 11879);
		pestleAndMortar = new ItemRequirement("Pestle and mortar", 233);
		adamantPlatebody = new ItemRequirement("Adamant platebody", 1123);
		adamantPlatelegs = new ItemRequirement("Adamant platelegs", 1073);
		bronzePlatelegs = new ItemRequirement("Bronze platelegs", 1075);
		bronzePlatebody = new ItemRequirement("Bronze platebody", 1117);
		mithrilSpear = new ItemRequirement("Mithril spear", 1243);
		steelPlatebody = new ItemRequirement("Steel platebody", 1119);
		ironDagger = new ItemRequirement("Iron dagger", 1203);
		bucketOfMilk = new ItemRequirement("Bucket of milk", 1927);
		patOfButter = new ItemRequirement("Pat of butter", 6697);
		potato = new ItemRequirement("Potato", 1942);
		bakedPotato = new ItemRequirement("Baked potato", 6701);
		potatoWithButter = new ItemRequirement("Potato with butter", 6703);
		potatoSeed = new ItemRequirement("Potato seed", 5318);
		rake = new ItemRequirement("Rake", 5341);
		seedDibber = new ItemRequirement("Seed dibber", 5343);
		compost = new ItemRequirement("Compost", 6032);
		softClay = new ItemRequirement("Soft clay", 1761);
		bronzePickaxe = new ItemRequirement("Bronze pickaxe", 1265);
		pinkSkirt = new ItemRequirement("Pink skirt", 1013);
		copperOre = new ItemRequirement("Copper ore", 436);
		feathers = new ItemRequirement("Feathers", ItemID.FEATHER);
		steelAxe = new ItemRequirement("Steel axe", ItemID.STEEL_AXE);
	}

	private void setupSteps()
	{
		// Section: Civitas illa Fortis [show when true: no condition]
		openleaguesmenu = new DetailedQuestStep(this, "Open the Leagues Menu.");
		completeleaguestutorial = new DetailedQuestStep(this, "Complete the Leagues Tutorial.");
		claimfirstrelic = new DetailedQuestStep(this, "Claim your first relic! If choosing Endless Harvest, turn off the auto-banking feature for now.");
		togglerunenergy = new DetailedQuestStep(this, "Turn run energy off and then back on.");
		pickpocketcitizen = new NpcStep(this, new int[]{13198, 13197, 13195}, new WorldPoint(1687, 3135, 0), "Pickpocket a citizen until level 5 thieving.", true);
		petrenu = new NpcStep(this, 13350, new WorldPoint(1697, 3142, 0), "Run east and pet Renu.", true);
		bownearrenu = new EmoteStep(this, QuestEmote.BOW, new WorldPoint(1697, 3140, 0), "Use the Bow emote near Renu.");
		bownearrenu.addIcon(QuestEmote.BOW.getSpriteId());
		travelwithrenu = new NpcStep(this, 13350, new WorldPoint(1697, 3142, 0), "Travel with Renu to the Sunset Coast.", true);
		travelwithrenu.addWidgetHighlight(874, 12, 8);
		travelbackwithrenu = new NpcStep(this, 14026, new WorldPoint(1389, 2899, 0), "Travel with Renu back to Civitas illa Fortis.", true);
		travelbackwithrenu.addWidgetHighlight(874, 12, 0);
		usewaterpump = new ObjectStep(this, 52646, new WorldPoint(1690, 3132, 0), "Fill a bucket at the water pump.", true);
		usewaterpump.addRequirement(bucket.highlighted());
		attackdummy = new ObjectStep(this, 52645, new WorldPoint(1659, 3148, 0), "Attack Dummy.", true);
		crynearachild = new EmoteStep(this, QuestEmote.CRY, new WorldPoint(1674, 3109, 0), "Use the Cry emote near a child.");
		crynearachild.addIcon(QuestEmote.CRY.getSpriteId());
		stealcakesfrombakersstall = new ObjectStep(this, 51937, new WorldPoint(1688, 3111, 0), "Thieve 14+ cakes from the Cake Stall.", true);
		{
			SkillRequirement sr = new SkillRequirement(Skill.THIEVING, 5, true);
			stealcakesfrombakersstall.addRequirement(sr);
		}
		tradeshopkeeper = new NpcStep(this, 13330, new WorldPoint(1670, 3123, 0), "Sell your cakes to the shopkeeper (sell 1 to maximize gp!). Buy 1 chisel and 1 knife.", true);
		tradeshopkeeper.addRequirement(cake);
		tradefloria = new NpcStep(this, 13340, new WorldPoint(1659, 3100, 0), "Buy 1 Pink Skirt, 1 Brown Apron, and 1 red Cape.", true);
		tradefloria.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.PINK_SKIRT, true);
		tradefloria.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.BROWN_APRON, true);
		tradefloria.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.RED_CAPE, true);
		equipapronandredcape = new DetailedQuestStep(this, "Equip the items you purchased.");
		equipapronandredcape.addRequirement(redCape.highlighted());
		equipapronandredcape.addRequirement(brownApron.highlighted());
		attackratlevel1 = new NpcStep(this, 2854, new WorldPoint(1655, 3084, 0), "Kill a rat with a bow.", true);
		salutequoatlos = new EmoteStep(this, QuestEmote.SALUTE, new WorldPoint(1699, 3083, 0), "Use the Salute emote near the statue of Quoatlos.");
		salutequoatlos.addIcon(SpriteID.Emotes.SALUTE);
		useprayernearshrineofralos = new ObjectStep(this, 52405, new WorldPoint(1701, 3085, 0), "Activate the Thick Skin prayer while standing next to the altar.", true);
		petxolo = new NpcStep(this, 12996, new WorldPoint(1709, 3088, 0), "Pet Xolo.", true);
		drinkfrombirdbath = new ObjectStep(this, 52394, new WorldPoint(1713, 3086, 0), "Drink-from Bird bath.", true);
		drinkfrombirdbath.addDialogStep("Yes.");
		chopdowntree = new ObjectStep(this, 52823, new WorldPoint(1707, 3090, 0), "Chop 2 logs from a nearby Tree. Make sure endless harvest is toggled off for banking.", true);
		burnlog = new DetailedQuestStep(this, "Burn a log.", logs.highlighted(), tinderbox.highlighted());
		fletchlog = new DetailedQuestStep(this, "Fletch a log into arrow shafts.", logs.highlighted(), knife.highlighted());
		fletchlog.addWidgetHighlightWithItemIdRequirement(270, 15, ItemID.ARROW_SHAFT, true);
		useeastcivitasbank = new NpcStep(this, 13219, new WorldPoint(1779, 3092, 0), "Deposit everything, withdraw coins, chisel, dramen staff (equipped), pot, and bucket of water.",
			true, coins, chisel, dramenStaff, pot, bucketOfWater);
		tradeartima = new NpcStep(this, 13342, new WorldPoint(1767, 3103, 0), "Buy shears and a glassblowing pipe from Artima.", true);
		tradeartima.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.SHEARS, true);
		tradeartima.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.GLASSBLOWINGPIPE, true);

		var inColloseum = new ZoneRequirement(new Zone(new WorldPoint(1795, 9480, 0), new WorldPoint(1880, 9547, 0)));
		var talktogladiatorstep  = new NpcStep(this, 12841, new WorldPoint(1802, 9523, 0), "Talk-to Gladiator.", true);
		var entergladiatorarea = new ObjectStep(this, 50749, new WorldPoint(1796, 3106, 0), "Enter the colloseum and talk to a Gladiator.");
		talktogladiator = new ConditionalStep(this, entergladiatorarea, "Talk to a Gladiator in the Colloseum.");
		talktogladiator.addStep(inColloseum, talktogladiatorstep);

		talktoportmaster = new NpcStep(this, 15470, new WorldPoint(1781, 3144, 0), "Talk-to Port master.", true);
		talktotradercrewmember = new NpcStep(this, 1330, new WorldPoint(1743, 3135, 0), "Buy 10 buckets of sand, 10 soda ash.", true);
		talktotradercrewmember.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.BUCKET_SAND, true);
		talktotradercrewmember.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.SODA_ASH, true);
		talktotradercrewmember2 = new NpcStep(this, 1330, new WorldPoint(1743, 3135, 0), "Buy 10 buckets of sand, 10 soda ash.", true);
		talktotradercrewmember2.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.BUCKET_SAND, true);
		talktotradercrewmember2.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.SODA_ASH, true);
		usefurnace = new ObjectStep(this, 50698, new WorldPoint(1769, 3111, 0), "Make 10 molten glass on the furnace south of the docks.", true);
		usefurnace2 = new ObjectStep(this, 50698, new WorldPoint(1769, 3111, 0), "Make 10 molten glass on the furnace south of the docks.", true);
		depositmoltenglass = new NpcStep(this, 13219, new WorldPoint(1779, 3092, 0), "Deposit 10 molten glass into the bank.", true);
		blowglassintooillanterns = new DetailedQuestStep(this, "Blow 20 molten glass into oil lamps.", moltenGlass.quantity(20).highlighted(), glassblowingPipe.highlighted());
		blowglassintooillanterns.addWidgetHighlightWithItemIdRequirement(270, 17, ItemID.OIL_LAMP_EMPTY, true);

		depositoillanterns = new NpcStep(this, 13219, new WorldPoint(1779, 3092, 0), "Deposit the 20 oil lanterns.", true);
		// Section: Outer Fortis [show when true: no condition]
		// 20372 0->1->2...->6 for cabbages
		pickcabbage = new ObjectStep(this, 1161, new WorldPoint(1742, 3059, 0), "Pick (and drop) 6 cabbages.", true);
		attackchickenlevel1 = new NpcStep(this, new int[]{1174, 1173}, new WorldPoint(1740, 3053, 0), "Kill a chicken and pick up its bones.", true);
		feedbonestomolossus = new NpcStep(this, 12993, new WorldPoint(1702, 3063, 0), "Feed some bones to Molossus.", true);
		feedbonestomolossus.addRequirement(bones.highlighted());
		feedbonestomolossus.addIcon(ItemID.BONES);
		picksweetcorn = new ObjectStep(this, 51829, new WorldPoint(1717, 3051, 0), "Pick sweetcorn.").addAlternateObjects(51830);
		pickandeatonion = new ObjectStep(this, 51837, new WorldPoint(1702, 3048, 0), "Pick an onion and eat it.", true, new ItemRequirement("Onion", ItemID.ONION).highlighted()).addAlternateObjects(51838);

		// Each wheat, 20371 0 up to 6
		pickwheat = new ObjectStep(this, 15506, new WorldPoint(1679, 3048, 0), "Pick 6 wheat. You can drop 5, but keep 1 to make flour.").addAlternateObjects(15507);

		var goUpToHopper = new ObjectStep(this, 52616, new WorldPoint(1655, 3043, 0), "Climb up the ladder in the windmill.");
		var goUpToHopper2 = new ObjectStep(this, 52619, new WorldPoint(1655, 3043, 1), "Climb up the ladder in the windmill.");
		var putInGrains = new ObjectStep(this, 52591, new WorldPoint(1653, 3046, 2), "Fill the hopper and use the controls.", true, grain.highlighted());
		putInGrains.addAlternateObjects(52592);
		var fillhopper = new ObjectStep(this, 52591, new WorldPoint(1653, 3046, 2), "Fill the hopper and use the controls.", true).addAlternateObjects(52592);
		fillhopper.addRequirement(grain.highlighted());

		var goDownGrain = new ObjectStep(this, 52621, new WorldPoint(1655, 3043, 2), "Climb downstairs.");
		var goDownGrain2 = new ObjectStep(this, 52619, new WorldPoint(1655, 3043, 1), "Climb down the ladder in the windmill.");
		var emptyFlour = new ObjectStep(this, 52999, new WorldPoint(1655, 3045, 0), "Empty Flour bin.", true, pot.highlighted());

		var flourMade = new VarbitRequirement(VarbitID.MILL_FLOUR, 1);
		var windmillf1 = new ZoneRequirement(new Zone(new WorldPoint(1652, 3042, 1), new WorldPoint(1659, 3049, 1)));
		var windmillf2 = new ZoneRequirement(new Zone(new WorldPoint(1652, 3042, 2), new WorldPoint(1659, 3049, 2)));
		emptyflourbin = new ConditionalStep(this, goUpToHopper, "Make flour in the windmill.");
		emptyflourbin.addStep(and(flourMade, windmillf2), goDownGrain);
		emptyflourbin.addStep(and(flourMade, windmillf1), goDownGrain2);
		emptyflourbin.addStep(flourMade, emptyFlour);
		emptyflourbin.addStep(windmillf2, putInGrains);
		emptyflourbin.addStep(windmillf1, goUpToHopper2);

		// 20373 0->6
		pickpotato = new ObjectStep(this, 312, new WorldPoint(1595, 3080, 0), "Pick (and drop) 6 potatoes.", true);
		tradeharminia = new NpcStep(this, 12766, new WorldPoint(1585, 3105, 0), "Buy a rake from Harminia.", true, coins.quantity(5));
		tradeharminia.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.RAKE, true);
		rakeflowerpatch = new ObjectStep(this, 50693, new WorldPoint(1585, 3098, 0), "Rake Flower Patch.", true);
		emptywaterbucket = new DetailedQuestStep(this, "Empty the bucket of water.");
		emptywaterbucket.addRequirement(bucketOfWater.highlighted());
		milkdairybuffalo = new ObjectStep(this, 52576, new WorldPoint(1588, 3119, 0), "Milk a Dairy Buffalo.", true, bucket);
		milkdairybuffalo2 = new ObjectStep(this, 52576, new WorldPoint(1588, 3119, 0), "Milk a Dairy Buffalo.", true, bucket);
		shearalpaca = new NpcStep(this, new int[]{12986, 12988}, new WorldPoint(1565, 3120, 0), "Shear an Alpaca.", true, shears);
		zanarisfairyring = new ObjectStep(this, 29495, new WorldPoint(1651, 3010, 0), "Take the Fairy Ring to Zanaris south of Civitas illa Fortis.", true);
		zanarisfairyring.addRequirement(dramenStaff.highlighted().equipped());
		attackcowlevel2 = new NpcStep(this, 5842, new WorldPoint(2432, 4446, 0), "Kill a Cow  (level-2), pick up the bones and cowhide.", true);
		burybones = new DetailedQuestStep(this, "Bury the Bones.");
		burybones.addRequirement(bones.highlighted());
		aldarinfairyring = new ObjectStep(this, 29560, new WorldPoint(2412, 4434, 0), "Take the Fairy Ring to Aldarin (CKQ).", true);
		aldarinfairyring.addRequirement(dramenStaff);
		talktovineyardforeman = new NpcStep(this, 13908, new WorldPoint(1370, 2915, 0), "Talk-to Vineyard foreman.", true);
		talktovineyardforeman.addDialogStep("Do you need any help in the vineyards?");
		collectshimmeringgrapes = new DetailedQuestStep(this, "Collect 4 shimmering grapes from the nearby vineyards (Vineyard Helper plugin can help if you have trouble identifying the shimmering grapes).");
		collectshimmeringgrapes.addRequirement(grapeBarrel);
		returntovineyardforeman = new NpcStep(this, 13908, new WorldPoint(1370, 2915, 0), "Return to the Vineyard foreman with a filled grape barrel.", true);
		returntovineyardforeman.addRequirement(grapeBarrel);
		withdrawaldarinbanker = new NpcStep(this, 13225, new WorldPoint(1401, 2928, 0), "Deposit everything and withdraw coins and chisel.", true, chisel, coins);
		withdrawaldarinbanker2 = new NpcStep(this, 13225, new WorldPoint(1401, 2928, 0), "Deposit everything and withdraw coins and chisel.", true);
		tradetoci = new NpcStep(this, 13915, new WorldPoint(1428, 2975, 0), "Buy, cut, and sell gems until you have more than a 50k gp cash stack.", true);
		cutextragems = new DetailedQuestStep(this, "Cut three extra of each gem and save them for later.");
		attackseagulllevel2 = new NpcStep(this, 1338, new WorldPoint(1439, 2967, 0), "Kill a Seagull  (level-2).", true);
		tradechartership = new NpcStep(this, new int[]{1329, 1332}, new WorldPoint(1454, 2969, 0), "Buy 5 raw rabbit, a tyras helm, and a bucket from the nearby Charter Ship.", true, rawRabbit.quantity(5), tyrasHelm, bucket);
		tradechartership.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.SAIL_TYRAS_HELM, true);
		tradechartership.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.RAW_RABBIT, true);
		tradechartership.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.BUCKET_EMPTY, true);

		cookrabbitonoven = new ObjectStep(this, 52647, new WorldPoint(1433, 2962, 0), "Cook the rabbit on the oven until you burn one and cook one successfully with an apron equipped.", true, brownApron.equipped());
		cookrabbitonoven.addRequirement(rawRabbit.quantity(5).highlighted());
		eatcookedrabbit = new DetailedQuestStep(this, "Eat the Cooked rabbit.");
		eatcookedrabbit.addRequirement(cookedRabbit.highlighted());
		// Section: Sunset Coast [show when true: no condition]
		talktoantonia = new NpcStep(this, 13985, new WorldPoint(1441, 2978, 0), "Travel with Antonia to the Sunset Coast.", true);
		talktoantonia.addRequirement(coins.quantity(20));
		talktoantonia2 = new NpcStep(this, 13985, new WorldPoint(1441, 2978, 0), "Travel with Antonia to the Sunset Coast.", true);
		talktoantonia2.addRequirement(coins.quantity(20));
		fillabucketwithsand = new ObjectStep(this, 50733, new WorldPoint(1520, 3001, 0), "Fill a bucket with sand on the Sunset Coast.", true);
		fillabucketwithsand.addIcon(ItemID.BUCKET_SAND);
		fillabucketwithsand.addRequirement(bucket.highlighted());
		talktopicaria = new NpcStep(this, 13158, new WorldPoint(1560, 2962, 0), "Buy 1000 feathers and 1 big fishing net from the fishing store southeast of the Sunset Coast.", true);
		talktopicaria.addRequirement(coins.quantity(2020));
		// Section: Civitas illa Fortis [show when true: no condition]
		chartertocivitas = new NpcStep(this, new int[]{1332, 1329}, new WorldPoint(1515, 2972, 0), "Charter a ship to Civitas illa Fortis.", true);
		chartertocivitas.addWidgetHighlight(72, 3, 27);
		chartertocivitas.addRequirement(coins.quantity(500));
		buyironmace = new NpcStep(this, 13333, new WorldPoint(1773, 3059, 0), "Purchase an iron mace from the Mace Shop southeast of Civitas illa Fortis.", true);
		buyironmace.addRequirement(coins.quantity(81));
		buyironmace.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.IRON_MACE, true);
		equipironmace = new DetailedQuestStep(this, "Equip the Iron mace.");
		equipironmace.addRequirement(ironMace.equipped().highlighted());
		equipironmace2 = new DetailedQuestStep(this, "Equip the Iron mace.");
		equipironmace2.addRequirement(ironMace.equipped().highlighted());
		castfishingspot = new NpcStep(this, 13329, new WorldPoint(1738, 3101, 0), "Fish at the fishing spot in the park until you have caught 1 casket, 1 boot, and 1 house key.", true);
		castfishingspot.addRequirement(bigFishingNet.highlighted());
		talktobartender = new NpcStep(this, 13344, new WorldPoint(1711, 3116, 0), "Buy 1 cup of tea, 1 Moon-lite, 1 stew, and 1 jug of wine from the nearby bar.", true);
		talktobartender.addRequirement(coins.quantity(46));
		talktobartender.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.CUP_OF_TEA, true);
		talktobartender.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.MOONLITE, true);
		talktobartender.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.STEW, true);
		talktobartender.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.JUG_WINE, true);
		drinkmoonlite = new DetailedQuestStep(this, "Drink the Moon-lite.");
		drinkmoonlite.addRequirement(moonLite.highlighted());
		drinkjugofwine = new DetailedQuestStep(this, "Drink the Jug of wine.");
		drinkjugofwine.addRequirement(jugOfWine);
		pickpocketcitizenuntilsuccess = new NpcStep(this, new int[]{13179, 13185}, new WorldPoint(1707, 3110, 0), "Pickpocket a Citizen until you have been successful 10 times in a row without failure.", true);
		buypoh = new NpcStep(this, 13343, new WorldPoint(1705, 3100, 0), "Purchase a Player-owned House from the Estate Agent.", true);
		buypoh.addDialogSteps("How can I get a house?", "Yes please!");
		buypoh.addRequirement(coins.quantity(1000));
		tradesilkmerchant = new NpcStep(this, 13334, new WorldPoint(1675, 3112, 0), "Buy silk and then sell it back to the Silk Merchant.", true);
		tradesilkmerchant.addRequirement(coins.quantity(15));
		givestewtooli = new NpcStep(this, 13318, new WorldPoint(1653, 3105, 0), "Give some stew to Oli near the west Civitas illa Fortis bank.", true);
		givestewtooli.addRequirement(stew.highlighted());
		givestewtooli.addIcon(ItemID.STEW);
		// Section: Auburnvale [show when true: no condition]
		traveltoauburnvale = new NpcStep(this, 13350, new WorldPoint(1697, 3142, 0), "Travel with Renu to Auburnvale.", true);
		traveltoauburnvale.addWidgetHighlight(874, 12, 12);
		depositinauburnvale = new NpcStep(this, 14671, new WorldPoint(1415, 3353, 0), "Deposit everything, withdraw coins, cowhide, wool, and bronze axe.", true, coins, cowhide, wool, bronzeAxe);
		tradesebamo = new NpcStep(this, 14658, new WorldPoint(1410, 3345, 0), "Buy a Staff of Air from the staff shop. Also buy a Staff of Fire and equip it.", true, staffOfAir, staffOfFire.equipped().highlighted());
		tradesebamo.addRequirement(coins.quantity(1500));
		tradesebamo.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.STAFF_OF_AIR, true);
		tradesebamo.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.STAFF_OF_FIRE, true);
		tradelunami = new NpcStep(this, 14657, new WorldPoint(1402, 3344, 0), "Buy a Steel, Mith, and Adamant axe from the axe shop.", true);
		tradelunami.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.STEEL_AXE, true);
		tradelunami.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.MITHRIL_AXE, true);
		tradelunami.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.ADAMANT_AXE, true);
		tradelunami.addRequirement(coins.quantity(2000));
		chopdowndeadtree = new ObjectStep(this, 1289, new WorldPoint(1406, 3331, 0), "Chop down a Dead tree from the river south of the Auburnvale bank.", true);
		chopdowndeadtree.addRequirement(bronzeAxe);
		makeplankatsawmill = new NpcStep(this, 14659, new WorldPoint(1395, 3369, 0), "Buy a normal plank from the Sawmill Operator.", true);
		makeplankatsawmill.addRequirement(logs);
		makeplankatsawmill.addWidgetHighlight(270, 15);
		makeplankatsawmill.addRequirement(coins.quantity(100));
		tanleatheratchouani = new NpcStep(this, 14662, new WorldPoint(1369, 3357, 0), "Talk-to Chouani to tan your cowhide into soft leather.", true);
		tanleatheratchouani.addRequirement(cowhide);
		tanleatheratchouani.addWidgetHighlight(324, 92);
		tanleatheratchouani.addRequirement(coins.quantity(2));
		tradeauburnvaleshopkeeper = new NpcStep(this, 14656, new WorldPoint(1379, 3352, 0), "Buy a needle and thread from the Auburnvale General Store.", true);
		tradeauburnvaleshopkeeper.addWidgetHighlight(324, 92);
		tradeauburnvaleshopkeeper.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.NEEDLE, true);
		tradeauburnvaleshopkeeper.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.THREAD, true);
		craftleatherchaps = new DetailedQuestStep(this, "Use Needle on Leather with Thread in your inventory to make Leather Chaps.");
		craftleatherchaps.addWidgetHighlight(270, 20);
		craftleatherchaps.addRequirement(leather.highlighted());
		craftleatherchaps.addRequirement(needle.highlighted());
		craftleatherchaps.addRequirement(thread);
		admirebeautifullog = new ObjectStep(this, 57112, new WorldPoint(1382, 3348, 0), "Admire Beautiful log.", true);
		steponenttrail = new ObjectStep(this, 57115, new WorldPoint(1395, 3340, 0), "Step on two nearby Ent Trails created behind a walking Ent.", true).addAlternateObjects(57116);
		// 20370, 0->6 for flax
		pickflax = new ObjectStep(this, 14896, new WorldPoint(1372, 3320, 0), "Pick 6 flax in the Nemus Retreat.", true);
		spinflaxandwool = new ObjectStep(this, 55330, new WorldPoint(1373, 3314, 0), "Spin 1 ball of wool and 1 bowstring.", true);
		spinflaxandwool.addRequirement(wool);
		spinflaxandwool.addRequirement(flax);
		hometeleport = new DetailedQuestStep(this, "Use the Home Teleport spell to Yama's lair.");
		hometeleport.addDialogStep("Yama's lair.");
		// Different to normal home teleport
		hometeleport.addWidgetHighlight(218, 5);
		returntocivitas = new ObjectStep(this, 11761, new WorldPoint(1503, 5570, 0), "Exit Yama's Lair to Civitas illa Fortis.");
		sitwithdogs = new EmoteStep(this, QuestEmote.SIT, new WorldPoint(1748, 3048, 0), "Use the Sit emote next to the cabbage being eaten by dogs.");
		sitwithdogs.addIcon(QuestEmote.SIT.getSpriteId());
		buytorch = new NpcStep(this, 13159, new WorldPoint(1515, 2984, 0), "Buy an unlit torch and a tinderbox from the General Store Shopkeeper.", true, unlitTorch, tinderbox);
		buytorch.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.TORCH_UNLIT, true);

		lighttorch = new DetailedQuestStep(this, "Light the Unlit torch.");
		lighttorch.addRequirement(unlitTorch.highlighted());
		lighttorch.addRequirement(tinderbox.highlighted());
		buypineapples = new NpcStep(this, 12799, new WorldPoint(1741, 3136, 0), "Buy 15 pineapples from the Trader Crewmember.", true);
		depositpineapples = new NpcStep(this, 13219, new WorldPoint(1779, 3092, 0), "Deposit the pineapples, lit torch, and tinderbox.", true);
		// TODO: Get varbit change?
		drinkcupoftea = new DetailedQuestStep(this, new WorldPoint(1434, 3321, 0), "Drink the Cup of tea in the Auburn Valley south of Auburnvale.");
		drinkcupoftea.addRequirement(cupOfTea);
		talktofriendlyforester = new NpcStep(this, 11427, new WorldPoint(1352, 3298, 0), "Talk to the Friendly Forester to claim a Forestry Kit, then equip it.", true, forestryKit.equipped().highlighted());
		talktofriendlyforester.addWidgetHighlight(819, 34, 0);
		// Section: Tal Teklan [show when true: no condition]
		traveltotalteklan = new NpcStep(this, 14729, new WorldPoint(1398, 3247, 0), "Travel by canoe with Achilka to Tal Teklan.", true);
		depositintalteklan = new NpcStep(this, 14778, new WorldPoint(1245, 3121, 0),
			"Deposit everything, withdraw coins, steel axe, feathers, and your iron mace (fire staff should be equipped).", true, coins, steelAxe, feathers, ironMace, staffOfFire.equipped());
		tradeteicuh = new NpcStep(this, 14775, new WorldPoint(1211, 3118, 0), "At the mage shop buy 5 of each elemental rune pack, 5 cosmic runes, 75 nature runes, 5 mind packs, 1 chaos pack, and 1 death rune.", true);
		tradexochitl = new NpcStep(this, 14774, new WorldPoint(1202, 3119, 0), "Purchase purple dye.", true);
		tradexochitl.addRequirement(coins.quantity(6));
		dyecape = new DetailedQuestStep(this, "Use the purple dye on your red cape to dye it.");
		dyecape.addRequirement(redCape.highlighted());
		dyecape.addRequirement(purpleDye.highlighted());
		dancenearabard = new EmoteStep(this, QuestEmote.DANCE, new WorldPoint(1190, 3112, 0), "Use the Dance emote near a Bard.");
		dancenearabard.addIcon(QuestEmote.DANCE.getSpriteId());
		tradearcuani = new NpcStep(this, 14776, new WorldPoint(1208, 3095, 0), "Buy a shortbow and 10+ iron arrows from Arcuani.", true);
		tradearcuani.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.SHORTBOW, true);
		tradearcuani.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.IRON_ARROW, true);
		attackfroglevel5 = new NpcStep(this, 8702, new WorldPoint(1314, 3105, 0), "Attack Frog  (level-5) with you shortbow. Fire at least 6 iron arrows to complete that task.", true);
		attackfroglevel5.addRequirement(shortbow);
		attackfroglevel5.addRequirement(ironArrow.quantity(10));
		chopdowndeadtreerainforest = new ObjectStep(this, 1286, new WorldPoint(1294, 3119, 0), "Chop down a Dead tree in the Tlati Rainforest.", true, steelAxe);
		traveltokastori = new NpcStep(this, 14728, new WorldPoint(1258, 3123, 0), "Travel by canoe with Achilka to Kastori.", true);
		tradesulisal = new NpcStep(this, 14762, new WorldPoint(1375, 3038, 0), "Buy 50 bait, 1 small fishing net, 1 fishing rod, 1 fly fishing rod, 1 lobster pot, and 1 harpoon from the fishing store southeast of the Sunset Coast.", true);
		tradesulisal.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.FISHING_BAIT, true);
		tradesulisal.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.NET, true);
		tradesulisal.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.FISHING_ROD, true);
		tradesulisal.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.FLY_FISHING_ROD, true);
		tradesulisal.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.HARPOON, true);
		tradesulisal.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.LOBSTER_POT, true);
		sardinefishingspot = new NpcStep(this, 14524, new WorldPoint(1333, 3010, 0), "Use your fishing rod to catch sardines at the fishing spot southwest of Kastori. Fill your inventory.", true);
		sardinefishingspot.addRequirement(fishingRod);
		sardinefishingspot.addRequirement(fishingBait);
		cooksardinesatoven = new ObjectStep(this, 57332, new WorldPoint(1356, 3037, 0), "Cook the raw sardines at the nearby Oven and then drop them.", true);
		cooksardinesatoven.addRequirement(rawSardine);
		shrimpfishingspot = new NpcStep(this, 14524, new WorldPoint(1333, 3010, 0), "Use your small fishing net to fish at the fishing spot until you reach level 10 fishing. Keep an inventory of shrimp to cook them.", true);
		shrimpfishingspot.addRequirement(smallFishingNet);
		cookshrimpatoven = new ObjectStep(this, 57332, new WorldPoint(1356, 3037, 0), "Cook the raw shrimp at the nearby oven.", true);
		cookshrimpatoven.addRequirement(rawShrimps);
		herringfishingspot = new NpcStep(this, 14524, new WorldPoint(1333, 3010, 0), "Use your fishing rod to catch herring until level 15 fishing.", true);
		herringfishingspot.addRequirement(fishingRod);
		herringfishingspot.addRequirement(fishingBait);
		anchovyfishingspot = new NpcStep(this, 14524, new WorldPoint(1333, 3010, 0), "Use your small fishing net to fish at the fishing spot until you catch an anchovy.", true);
		anchovyfishingspot.addRequirement(smallFishingNet);
		climbdownladder = new ObjectStep(this, 56469, new WorldPoint(1378, 3054, 0), "Climb-down the Ladder marked by a Dungeon icon on the map in the northeast of Kastori.", true);
		attackimplevel2 = new NpcStep(this, 14207, new WorldPoint(1379, 9455, 0), "Use an earth spell to defeat an  Imp  (level-2). Loot the ashes, but do not scatter them yet.", true);
		climbupladder = new ObjectStep(this, 56468, new WorldPoint(1378, 9454, 0), "Climb-up Ladder.", true);
		petcaique = new NpcStep(this, 14572, new WorldPoint(1369, 3081, 0), "Pet Caique.", true);
		activatestatuekastori = new ObjectStep(this, 56918, new WorldPoint(1368, 3087, 0), "Activate the statue north of Kastori.", true);
		attackgemstonecrablevel160 = new NpcStep(this, 14779, new WorldPoint(1353, 3112, 0), "Attack the Gemstone Crab  (level-160) with until you reach level 20 attack, 21 magic, and 5 defense.", true);
		witnessthegemcrabsdefeat = new DetailedQuestStep(this, "Witness the Gemstone Crab's defeat.");
		travelgloomthorn = new NpcStep(this, 14727, new WorldPoint(1388, 3073, 0), "Travel by canoe with Achilka to Gloomthorn Trail.", true);
		activatestatuenemus = new ObjectStep(this, 56921, new WorldPoint(1362, 3276, 0), "Activate the statue south of the Nemus Retreat.", true);
		attackicefiendlevel13 = new NpcStep(this, 4813, new WorldPoint(1434, 3260, 0), "Kill an Icefiend  (level-13) in Mons Gratia.", true);
		activatestatuedarkfrost = new ObjectStep(this, 54544, new WorldPoint(1491, 3282, 0), "Activate the statue near the base of the Darkfrost.", true);
		attackrabbitlevel2 = new NpcStep(this, new int[]{12851, 12852}, new WorldPoint(1493, 3225, 0), "Kill 5 Rabbits  (level-2) in the Quetzacalli Gorge with the iron mace and bury their bones.", true);
		yamaagility = new ObjectStep(this, 13621, new WorldPoint(1498, 5581, 0), "Complete 666 jumps at the Yama agility course.", true);
		travelaldarin = new NpcStep(this, 13350, new WorldPoint(1697, 3142, 0), "Travel with Renu to Aldarin.", true);
		travelaldarin.addWidgetHighlight(874, 12, 8);
		withdrawtyrashelm = new NpcStep(this, 13225, new WorldPoint(1401, 2928, 0), "Withdraw the Tyras Helm from your bank and equip it.", true);
		withdrawtyrashelm.addRequirement(tyrasHelm);
		enterportal = new ObjectStep(this, 55353, new WorldPoint(1420, 2962, 0), "Enter the House Portal in Build Mode.", true);
		buildroom = new DetailedQuestStep(this, "Build the cheapest room in your POH.");
		buildroom.addWidgetHighlight(212, 5);
		buildroom.addDialogStep("Build");
		buildroom.addRequirement(coins.quantity(1000));
		exitportal = new ObjectStep(this, 4525, new WorldPoint(1860, 7051, 0), "Exit the house back to Aldarin.", true);
		tradetociagain = new NpcStep(this, 13915, new WorldPoint(1427, 2974, 0), "Buy uncut gems, cut them, and sell them back to Toci until you reach level 70 Crafting.", true);
		tradethurid = new NpcStep(this, 13160, new WorldPoint(1512, 2994, 0), "Buy a bronze full helmet and an adamant full helm from the helmet shop in the Sunset Coast.", true);
		tradethurid.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.BRONZE_FULL_HELM, true);
		tradethurid.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.ADAMANT_FULL_HELM, true);
		buyfishfrompicaria = new NpcStep(this, 13158, new WorldPoint(1559, 2959, 0), "Buy fish from the fishing store southeast of the Sunset Coast, cook them on the oven nearby, and drop them until you reach 50 cooked tuna and 100 cooked swordfish.", true);
		// Section: Cam Torum [show when true: no condition]
		entercamtorum = new ObjectStep(this, 51375, new WorldPoint(1435, 3129, 0), "Run north and enter Cam Torum.", true);
		tradehuito = new NpcStep(this, 13041, new WorldPoint(1437, 9551, 1), "Buy 1 water-filled vial pack, 1 pestle and mortar, and 1 eye of newt pack at the herb shop.", true);
		tradehuito.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.PACK_VIAL_WATER, true);
		tradehuito.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.PESTLE_AND_MORTAR, true);
		tradehuito.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.PACK_EYE_NEWT, true);
		tradetizoro = new NpcStep(this, 13037, new WorldPoint(1441, 9554, 1), "Buy a steel, mithril, adamant, and rune pickaxe from the mining shop. (For Barbarian Gatherer's, there are some steps requiring pickaxes. Not sure if they will count without the pickaxe.)", true);
		tradetizoro.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.STEEL_PICKAXE, true);
		tradetizoro.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.MITHRIL_PICKAXE, true);
		tradetizoro.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.ADAMANT_PICKAXE, true);
		tradetizoro.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.RUNE_PICKAXE, true);

		haircutsonalo = new NpcStep(this, 13042, new WorldPoint(1452, 9555, 1), "Get a Maroon-colored Samurai haircut from the hairstylist to the east.", true);
		haircutsonalo.addDialogStep("I'd like a haircut please.");
		haircutsonalo.addWidgetHighlight(516, 27);
		shavesonalo = new NpcStep(this, 13042, new WorldPoint(1450, 9557, 1), "Trim your beard and select the clean shaven option from Sonalo.", true);
		shavesonalo.addDialogStep("I'd like a shave please.");
		shavesonalo.addWidgetHighlight(516, 29);
		shavesonalo.addWidgetHighlight(516, 31, 12);
		shavesonalo.addWidgetHighlight(516, 27);
		tradenahta = new NpcStep(this, 13036, new WorldPoint(1426, 9569, 1), "Buy 50 law runes, 500 nature runes, 100 death runes, and extra elemental runes if needed from the rune shop.", true);
		tradenahta.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.DEATHRUNE, true);
		tradenahta.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.NATURERUNE, true);
		tradenahta.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.LAWRUNE, true);
		lowalchrunes = new DetailedQuestStep(this, "Cast low alch on an earth rune for mage xp.");
		depositcamtorumbanker = new NpcStep(this, 13044, new WorldPoint(1451, 9570, 1), "Deposit everything but the fire runes, nature runes, and coins.", true);
		tradecamtorumbartender = new NpcStep(this, 13043, new WorldPoint(1432, 9583, 1), "Trade the Bartender and buy 5 jugs of wine.", true);
		tradecamtorumbartender.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.JUG_WINE, true);
		tradecamtorumblacksmith = new NpcStep(this, 13038, new WorldPoint(1447, 9583, 1), "Buy an iron dagger from the Cam Torum Blacksmith and equip it.", true, ironDagger.equipped().highlighted());
		tradecamtorumblacksmith.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.IRON_DAGGER, true);
		// Section: Civitas illa Fortis [show when true: no condition]
		leaguemenuteleport = new DetailedQuestStep(this, "Home teleport to Civitas illa Fortis.");
		leaguemenuteleport.addDialogStep("Civitas illa Fortis.");
		// Different to normal home teleport
		leaguemenuteleport.addWidgetHighlight(218, 5);
		tradefortisblacksmith = new NpcStep(this, 13332, new WorldPoint(1656, 3141, 0), "Buy an adamant platebody/legs, bronze platebody/legs, mithril spear, and steel platebody from the Fortis Blacksmith.", true);
		tradefortisblacksmith.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.ADAMANT_PLATEBODY, true);
		tradefortisblacksmith.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.ADAMANT_PLATELEGS, true);
		tradefortisblacksmith.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.BRONZE_PLATEBODY, true);
		tradefortisblacksmith.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.BRONZE_PLATELEGS, true);
		tradefortisblacksmith.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.MITHRIL_SPEAR, true);
		tradefortisblacksmith.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.STEEL_PLATEBODY, true);

		equipmithrilspear = new DetailedQuestStep(this, "Equip the Mithril spear.");
		equipmithrilspear.addRequirement(mithrilSpear.equipped().highlighted());
		lowalchsteelplatebody = new DetailedQuestStep(this, "Low alch the steel platebody.");
		lowalchsteelplatebody.addRequirement(steelPlatebody.highlighted());
		depositfortiswestbank = new NpcStep(this, 13213, new WorldPoint(1647, 3120, 0), "Deposit the adamant and bronze platebodies/legs, and the iron dagger. Withdraw 15 pineapples, a bucket, a big fishing net, and runes for wind bolt, pickaxes, and mithril spear.", true);
		depositfortiswestbank.addWidgetHighlightWithItemIdRequirement(12, 12, ItemID.PINEAPPLE, true);
		depositfortiswestbank.addWidgetHighlightWithItemIdRequirement(12, 12, ItemID.BUCKET_EMPTY, true);
		depositfortiswestbank.addWidgetHighlightWithItemIdRequirement(12, 12, ItemID.BIG_NET, true);
		depositfortiswestbank.addWidgetHighlightWithItemIdRequirement(12, 12, ItemID.AIRRUNE, true);
		depositfortiswestbank.addWidgetHighlightWithItemIdRequirement(12, 12, ItemID.CHAOSRUNE, true);
		depositfortiswestbank.addWidgetHighlightWithItemIdRequirement(12, 12, ItemID.JUG_WINE, true);
		depositfortiswestbank.addWidgetHighlightWithItemIdRequirement(12, 12, ItemID.BRONZE_PICKAXE, true);
		depositfortiswestbank.addWidgetHighlightWithItemIdRequirement(12, 12, ItemID.STEEL_PICKAXE, true);
		depositfortiswestbank.addWidgetHighlightWithItemIdRequirement(12, 12, ItemID.MITHRIL_PICKAXE, true);
		depositfortiswestbank.addWidgetHighlightWithItemIdRequirement(12, 12, ItemID.ADAMANT_PICKAXE, true);
		depositfortiswestbank.addWidgetHighlightWithItemIdRequirement(12, 12, ItemID.RUNE_PICKAXE, true);
		depositfortiswestbank.addWidgetHighlightWithItemIdRequirement(12, 12, ItemID.MITHRIL_SPEAR, true);
		enterguardbunker = new ObjectStep(this, 52641, new WorldPoint(1640, 3165, 0), "Climb-down the staircase to the north into the Guard's Bunker.", true);
		attackguardlevel21 = new NpcStep(this, new int[]{13105, 13100, 13107, 13109, 13101, 13108, 13104, 13103, 13102}, new WorldPoint(1639, 9564, 0), "Kill a Guard  (level-21).", true);
		exitguardbunker = new ObjectStep(this, 52642, new WorldPoint(1640, 9564, 0), "Climb-up the staircase to exit the Guard's Bunker.", true);
		enterthiefsden = new ObjectStep(this, 52625, new WorldPoint(1614, 3175, 0), "Run around the outside western wall of Civitas illa Fortis to enter the Thief's Den west of the Guard's Bunker.", true);
		attackthieflevel16 = new NpcStep(this, new int[]{13294, 13295, 13293}, new WorldPoint(1622, 9573, 0), "Kill a Thief  (level-16).", true);
		exitthiefsden = new ObjectStep(this, 52624, new WorldPoint(1615, 9575, 0), "Climb-up the ladder to exit the Thief's Den.", true);
		// Section: Varlamore [show when true: no condition]
		attackbuffalolevel9 = new NpcStep(this, 13004, new WorldPoint(1587, 3116, 0), "Kill a Buffalo  (level-9).", true);
		churndairychurn = new ObjectStep(this, 11695, new WorldPoint(1602, 3098, 0), "Churn some butter at the Dairy churn southeast of the Dairy Buffalo. Keep it for a potato.", true);
		churndairychurn.addRequirement(bucketOfMilk);
		churndairychurn.addWidgetHighlight(270, 16);
		pickpotato2 = new ObjectStep(this, 312, new WorldPoint(1595, 3080, 0), "Pick a potato from the potato patch nearby.");
		cookfarmingpatchoven = new ObjectStep(this, 52647, new WorldPoint(1590, 3105, 0), "Cook the potato at the Oven near the farming patches.", true);
		cookfarmingpatchoven.addRequirement(potato);
		combinebutterwithpotato = new DetailedQuestStep(this, "Use the Pat of butter on the Baked Potato.");
		combinebutterwithpotato.addRequirement(patOfButter.highlighted());
		combinebutterwithpotato.addRequirement(bakedPotato.highlighted());
		fillcompostbin = new ObjectStep(this, 7808, new WorldPoint(1588, 3103, 0), "Put the 15 pineapples in the compost bin, then close it.", true);
		fillcompostbin.addRequirement(pineapple.quantity(15));
		pickpocketmasterfarmer = new NpcStep(this, 13231, new WorldPoint(1595, 3087, 0), "Pickpocket the Farmer to the south until you have 3 potato seeds.", true);
		buyseeddibberharminia = new NpcStep(this, 12766, new WorldPoint(1586, 3102, 0), "Buy a rake, a seed dibber, and two buckets of compost from Harminia.", true);
		buyseeddibberharminia.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.RAKE, true);
		buyseeddibberharminia.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.DIBBER, true);
		buyseeddibberharminia.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.BUCKET_COMPOST, true);
		rakeallotment = new ObjectStep(this, 50695, new WorldPoint(1585, 3095, 0), "Rake Allotment.", true);
		rakeallotment.addRequirement(rake);
		plantpotatoseeds = new ObjectStep(this, 50695, new WorldPoint(1585, 3095, 0), "Plant some potato seeds in the allotment.", true);
		plantpotatoseeds.addIcon(ItemID.POTATO_SEED);
		plantpotatoseeds.addRequirement(potatoSeed.quantity(3).highlighted());
		plantpotatoseeds.addRequirement(seedDibber);
		payprotectharminia = new NpcStep(this, 12766, new WorldPoint(1580, 3100, 0), "Pay Harminia the two buckets of compost to protect the patch.", true, compost.quantity(2));
		attackdirewolflevel88 = new NpcStep(this, 3426, new WorldPoint(1503, 3122, 0), "Safespot a Dire Wolf  (level-88) with magic from behind some plants.", true);
		attackscorpionlevel14 = new NpcStep(this, 13001, new WorldPoint(1489, 3085, 0), "Kill a Scorpion  (level-14) with the mithril spear near the mining area to the southwest.", true);
		attackscorpionlevel14.addRequirement(mithrilSpear.equipped());
		minecopperrocks = new ObjectStep(this, 10943, new WorldPoint(1483, 3089, 0), "Mine at least 1 Copper ore.", true).addAlternateObjects(11161);
		minecopperrocks.addRequirement(bronzePickaxe);
		minetinrocks = new ObjectStep(this, 11361, new WorldPoint(1483, 3092, 0), "Mine at least 6 Tin ore, continue dropping and mining until level 15 mining.", true).addAlternateObjects(11360);
		minetinrocks.addRequirement(bronzePickaxe);
		attackjaguarlevel67 = new NpcStep(this, 12976, new WorldPoint(1577, 2985, 0), "Safespot a Jaguar  (level-67) to the southeast without taking damage.", true);
		attackhillgiantlevel28 = new NpcStep(this, new int[]{12848, 12849, 12850}, new WorldPoint(1617, 2965, 0), "Kill a Hill Giant  (level-28) to the southeast.", true);
		var steelPickaxe = new ItemRequirement("Steel pickaxe", ItemID.STEEL_PICKAXE);
		mineclayrocks = new ObjectStep(this, 11362, new WorldPoint(1753, 2958, 0), "Mine 1 clay at the Stonecutter's Outpost and keep it.", true, steelPickaxe).addAlternateObjects(11363);
		mineironrocks = new ObjectStep(this, 11365, new WorldPoint(1744, 2958, 0), "Mine 50 iron and drop it.", true, steelPickaxe).addAlternateObjects(11364);
		minesilverrocks = new ObjectStep(this, 11369, new WorldPoint(1743, 2950, 0), "Mine 20 silver and drop it.", true, steelPickaxe).addAlternateObjects(11368);
		minecoalrocks = new ObjectStep(this, 11367, new WorldPoint(1735, 2949, 0), "Mine 2 coal and drop it.", true, steelPickaxe);
		minegoldrocks = new ObjectStep(this, 11370, new WorldPoint(1746, 2957, 0), "Mine 3 gold and keep it. Skip step in sidebar once you've got them.", true, steelPickaxe);
		minemoreiron = new ObjectStep(this, 11365, new WorldPoint(1744, 2958, 0), "Mine more iron until level 55 mining.", true).addAlternateObjects(11364);
		minemithrilrocks = new ObjectStep(this, 11372, new WorldPoint(1744, 2947, 0), "Mine 50 mithril and drop it.", true);
		bignetcoastfishingspot = new NpcStep(this, 12776, new WorldPoint(1771, 2966, 0), "Use your Big Fishing Net to catch and drop 20 mackerel at the fishing spot on the coast to the east.", true);
		bignetcoastfishingspot.addRequirement(bigFishingNet);
		bignetcoastfishingspotcod = new NpcStep(this, 12776, new WorldPoint(1771, 2966, 0), "Use your Big Fishing Net to catch and drop 10 cod at the fishing spot on the coast to the east.", true);
		bignetcoastfishingspotcod.addRequirement(bigFishingNet);
		attackoryxlevel15 = new NpcStep(this, 13006, new WorldPoint(1726, 3029, 0), "Kill an Oryx  (level-15) with a melee weapon.", true);
		// Section: Civitas illa Fortis [show when true: no condition]
		buymouldsartima = new NpcStep(this, 13342, new WorldPoint(1766, 3101, 0), "Buy 1 tiara mould, 1 ring mould, and 1 amulet mould from Artima in the crafting shop inside Civitas illa Fortis.", true);
		buymouldsartima.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.TIARA_MOULD, true);
		buymouldsartima.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.RING_MOULD, true);
		buymouldsartima.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.AMULET_MOULD, true);

		softclayspawn = new ObjectStep(this, 52635, new WorldPoint(1766, 3099, 0), "Check if the soft clay spawn upstairs is available, if not make your own with the bank nearby.", true, softClay);
		usepotterswheel = new ObjectStep(this, 50732, new WorldPoint(1763, 3096, 0), "Make anything on the Potter's Wheel.");
		usepotterswheel.addWidgetHighlightWithItemIdRequirement(270, 15, ItemID.POT_UNFIRED, true);
		usepotterswheel.addRequirement(softClay);
		firepotteryoven = new ObjectStep(this, 11601, new WorldPoint(1764, 3094, 0), "Fire the unfired pottery at the Pottery Oven.");
		thankfaux = new DetailedQuestStep(this, "Say thank you to Faux at twitch.tv/Faux. There's additional steps on his guide, but this is as far as we can take you. Good luck!");

	}

	@Override
	public QuestStep loadStep()
	{
		initializeRequirements();
		setupSteps();

		openleaguesmenu.withPersistedManualSkip("orderManual_3af37088880c4e47");
		completeleaguestutorial.withPersistedManualSkip("orderManual_29c81dc8336344ac");
		claimfirstrelic.withPersistedManualSkip("orderManual_b4774b5043644e3d");
		togglerunenergy.withPersistedManualSkip("orderManual_e9f51a0871734a55");
		pickpocketcitizen.withPersistedManualSkip("orderManual_acbdd227965c4943");
		petrenu.withPersistedManualSkip("orderManual_d2d9922879654882");
		bownearrenu.withPersistedManualSkip("orderManual_e1bd7473bf70454e");
		travelwithrenu.withPersistedManualSkip("orderManual_3742c1b9593940d1");
		travelbackwithrenu.withPersistedManualSkip("orderManual_d874440fd5d64f29");
		usewaterpump.withPersistedManualSkip("orderManual_3e3dee13c7f04f7e");
		attackdummy.withPersistedManualSkip("orderManual_114b66be530d46b0");
		crynearachild.withPersistedManualSkip("orderManual_19a411c69a9a454a");
		stealcakesfrombakersstall.withPersistedManualSkip("orderManual_561cc210c13940ac");
		tradeshopkeeper.withPersistedManualSkip("orderManual_6231965cb7364873");
		tradefloria.withPersistedManualSkip("orderManual_ef2a1f04f2344ffa");
		equipapronandredcape.withPersistedManualSkip("orderManual_62fd4ddcb8574922");
		attackratlevel1.withPersistedManualSkip("orderManual_78e2364d79f24bf0");
		salutequoatlos.withPersistedManualSkip("orderManual_de139d6c81f54075");
		useprayernearshrineofralos.withPersistedManualSkip("orderManual_b4f635703c864239");
		petxolo.withPersistedManualSkip("orderManual_e9ba959f7cfe48c5");
		drinkfrombirdbath.withPersistedManualSkip("orderManual_db130671c0de4eac");
		chopdowntree.withPersistedManualSkip("orderManual_3d13ecb7d4b94ca8");
		burnlog.withPersistedManualSkip("orderManual_bcef081ee50848c4");
		fletchlog.withPersistedManualSkip("orderManual_6691ac72f00a43ba");
		useeastcivitasbank.withPersistedManualSkip("orderManual_b6371840b279439e");
		tradeartima.withPersistedManualSkip("orderManual_33caa0d56cbf4e3a");
		talktogladiator.withPersistedManualSkip("orderManual_0b5d40160f57421a");
		talktoportmaster.withPersistedManualSkip("orderManual_38fb64dc8cff4589");
		talktotradercrewmember.withPersistedManualSkip("orderManual_23938df7e41444d2");
		talktotradercrewmember2.withPersistedManualSkip("orderManual_b3170a80123940bc");
		usefurnace.withPersistedManualSkip("orderManual_e3d64188fe2c4386");
		usefurnace2.withPersistedManualSkip("orderManual_b4898e48afbc4790");
		depositmoltenglass.withPersistedManualSkip("orderManual_0643bdd0fc574df9");
		blowglassintooillanterns.withPersistedManualSkip("orderManual_4ed627e87870495c");

		
		pickcabbage.withPersistedManualSkip("orderManual_925b2750606e45aa");
		attackchickenlevel1.withPersistedManualSkip("orderManual_e2187282abd14612");
		feedbonestomolossus.withPersistedManualSkip("orderManual_7307d7a1545249c7");
		picksweetcorn.withPersistedManualSkip("orderManual_68855f46f9bc41df");
		pickandeatonion.withPersistedManualSkip("orderManual_03a02189ef2c41c9");
		pickwheat.withPersistedManualSkip("orderManual_f959773416174f78");
		emptyflourbin.withPersistedManualSkip("orderManual_7e7d7d1aa70f4206");
		pickpotato.withPersistedManualSkip("orderManual_956b7e8f1cba43e0");
		tradeharminia.withPersistedManualSkip("orderManual_397839f1b1f24846");
		rakeflowerpatch.withPersistedManualSkip("orderManual_5caee225521442f9");
		emptywaterbucket.withPersistedManualSkip("orderManual_00554878070f4f0e");
		milkdairybuffalo.withPersistedManualSkip("orderManual_4c110a72a4b748d1");
		milkdairybuffalo2.withPersistedManualSkip("orderManual_22e9386da6d3466c");
		shearalpaca.withPersistedManualSkip("orderManual_6fb5cb3f33894336");
		zanarisfairyring.withPersistedManualSkip("orderManual_d30aa746056444c3");
		attackcowlevel2.withPersistedManualSkip("orderManual_c75f7a1c32ef4f3f");
		burybones.withPersistedManualSkip("orderManual_8842f3b65fdb4d05");
		aldarinfairyring.withPersistedManualSkip("orderManual_0819bafed42f4b91");
		talktovineyardforeman.withPersistedManualSkip("orderManual_7060fc9ca3444a25");
		collectshimmeringgrapes.withPersistedManualSkip("orderManual_b4066f8970574c8a");
		returntovineyardforeman.withPersistedManualSkip("orderManual_9c6304e17f074f11");
		withdrawaldarinbanker.withPersistedManualSkip("orderManual_04453a244b8e4e28");
		withdrawaldarinbanker2.withPersistedManualSkip("orderManual_4decff0f320c4771");
		tradetoci.withPersistedManualSkip("orderManual_2a4533618e3843cc");
		cutextragems.withPersistedManualSkip("orderManual_864a7b35392e4bb5");
		attackseagulllevel2.withPersistedManualSkip("orderManual_b1d18d00302b44e1");
		tradechartership.withPersistedManualSkip("orderManual_39b2b856b31b46a8");
		cookrabbitonoven.withPersistedManualSkip("orderManual_bd0d9307875d4407");
		talktoantonia.withPersistedManualSkip("orderManual_eab67f10eb07403e");
		talktoantonia2.withPersistedManualSkip("orderManual_aa9fdb2b71f245c6");
		fillabucketwithsand.withPersistedManualSkip("orderManual_9b1eca8dccbd42d2");
		chartertocivitas.withPersistedManualSkip("orderManual_defad039d9094571");
		buyironmace.withPersistedManualSkip("orderManual_52df5d410a9c4609");
		equipironmace.withPersistedManualSkip("orderManual_71753357153f4aea");
		equipironmace2.withPersistedManualSkip("orderManual_973a0e20125a4986");
		castfishingspot.withPersistedManualSkip("orderManual_7279930e175347d8");
		talktobartender.withPersistedManualSkip("orderManual_2d00f499fa504f2c");
		drinkmoonlite.withPersistedManualSkip("orderManual_69fff6adc2d94fa7");
		drinkjugofwine.withPersistedManualSkip("orderManual_3a1994fd479f4789");
		pickpocketcitizenuntilsuccess.withPersistedManualSkip("orderManual_304f7ffb27f04f3a");
		buypoh.withPersistedManualSkip("orderManual_67ac90c160da4e80");
		tradesilkmerchant.withPersistedManualSkip("orderManual_6d81d68a300a421e");
		traveltoauburnvale.withPersistedManualSkip("orderManual_2db5a3f19d944160");
		depositinauburnvale.withPersistedManualSkip("orderManual_fc2cc76a69004247");
		tradesebamo.withPersistedManualSkip("orderManual_98baa1e71c7749f8");
		tradelunami.withPersistedManualSkip("orderManual_4aa9260549cf41b7");
		chopdowndeadtree.withPersistedManualSkip("orderManual_dfe5b2e820144145");
		makeplankatsawmill.withPersistedManualSkip("orderManual_b30a9b21a43c4399");
		tanleatheratchouani.withPersistedManualSkip("orderManual_558232d615c64c3e");
		tradeauburnvaleshopkeeper.withPersistedManualSkip("orderManual_b9bdb796f67b407a");
		craftleatherchaps.withPersistedManualSkip("orderManual_8bc30c12632a4f04");
		admirebeautifullog.withPersistedManualSkip("orderManual_18df7577e9814dd7");
		steponenttrail.withPersistedManualSkip("orderManual_6ec91f1a29aa464f");
		pickflax.withPersistedManualSkip("orderManual_d51c833717d64aae");
		spinflaxandwool.withPersistedManualSkip("orderManual_03d13e43583345cb");
		hometeleport.withPersistedManualSkip("orderManual_a1a431788a17478e");
		returntocivitas.withPersistedManualSkip("orderManual_e2c6f51feb6142eb");
		sitwithdogs.withPersistedManualSkip("orderManual_804de90e08e84b59");
		buytorch.withPersistedManualSkip("orderManual_f044d2ea29444c1d");
		lighttorch.withPersistedManualSkip("orderManual_f857afb94063482c");
		buypineapples.withPersistedManualSkip("orderManual_be85462b242840cf");
		depositpineapples.withPersistedManualSkip("orderManual_e5d473eb18d64808");
		drinkcupoftea.withPersistedManualSkip("orderManual_4663556c4ad648bf");
		traveltotalteklan.withPersistedManualSkip("orderManual_d788a0be7313479e");
		depositintalteklan.withPersistedManualSkip("orderManual_01b9f9d76bc94c60");
		tradeteicuh.withPersistedManualSkip("orderManual_1171a596083a4eaf");
		tradexochitl.withPersistedManualSkip("orderManual_e4ec7a5e93f94c50");
		dyecape.withPersistedManualSkip("orderManual_a62e5ad5b779487c");
		dancenearabard.withPersistedManualSkip("orderManual_cfbbc6296cf34f41");
		tradearcuani.withPersistedManualSkip("orderManual_3775ef8059c3414d");
		attackfroglevel5.withPersistedManualSkip("orderManual_37f5de97d3244bc3");
		chopdowndeadtreerainforest.withPersistedManualSkip("orderManual_d2c3db388c6e488e");
		traveltokastori.withPersistedManualSkip("orderManual_144de147b4dd429f");
		tradesulisal.withPersistedManualSkip("orderManual_c9641ef94bed4e57");
		sardinefishingspot.withPersistedManualSkip("orderManual_7ea35541def3437c");
		cooksardinesatoven.withPersistedManualSkip("orderManual_289f278fcdbc441c");
		shrimpfishingspot.withPersistedManualSkip("orderManual_d783206aa2684ec7");
		cookshrimpatoven.withPersistedManualSkip("orderManual_fe6e163c06804219");
		herringfishingspot.withPersistedManualSkip("orderManual_4bd4c4ce6db44382");
		anchovyfishingspot.withPersistedManualSkip("orderManual_7b3fbad760644c7e");
		climbdownladder.withPersistedManualSkip("orderManual_3ab921c32aa643b3");
		attackimplevel2.withPersistedManualSkip("orderManual_4302669db12948af");
		climbupladder.withPersistedManualSkip("orderManual_4177e886a0f0431a");
		petcaique.withPersistedManualSkip("orderManual_7b8ba95ea0d64517");
		activatestatuekastori.withPersistedManualSkip("orderManual_ec5549b44450458a");
		attackgemstonecrablevel160.withPersistedManualSkip("orderManual_8e45e36fcaaa417e");
		witnessthegemcrabsdefeat.withPersistedManualSkip("orderManual_11db367c6081408e");
		travelgloomthorn.withPersistedManualSkip("orderManual_040bffb8e5d14642");
		activatestatuenemus.withPersistedManualSkip("orderManual_f4f064c3ad3f44eb");
		attackicefiendlevel13.withPersistedManualSkip("orderManual_f98483f36a484e18");
		activatestatuedarkfrost.withPersistedManualSkip("orderManual_84de0662250c4030");
		attackrabbitlevel2.withPersistedManualSkip("orderManual_f679c2afb8924312");
		yamaagility.withPersistedManualSkip("orderManual_4a6cc9352e4b414d");
		travelaldarin.withPersistedManualSkip("orderManual_822d7bebf7814a0d");
		withdrawtyrashelm.withPersistedManualSkip("orderManual_c7dfc0ff11a544e1");
		enterportal.withPersistedManualSkip("orderManual_dc1ebad629a3492e");
		buildroom.withPersistedManualSkip("orderManual_8a40feb623344dbe");
		exitportal.withPersistedManualSkip("orderManual_78aae6dc2b1e4784");
		tradetociagain.withPersistedManualSkip("orderManual_e7b7eeb41929445f");
		tradethurid.withPersistedManualSkip("orderManual_34bc6f2756944267");
		entercamtorum.withPersistedManualSkip("orderManual_6399630404814616");
		tradehuito.withPersistedManualSkip("orderManual_49aa6ca9547f4410");
		tradetizoro.withPersistedManualSkip("orderManual_796ca0c49092421f");
		haircutsonalo.withPersistedManualSkip("orderManual_eb1a1275094e4580");
		shavesonalo.withPersistedManualSkip("orderManual_e0c4fad1ef074063");
		tradenahta.withPersistedManualSkip("orderManual_722758b4ebad4b90");
		lowalchrunes.withPersistedManualSkip("orderManual_47586e99ca544eea");
		depositcamtorumbanker.withPersistedManualSkip("orderManual_6403213cd38e4c6e");
		tradecamtorumbartender.withPersistedManualSkip("orderManual_61db14d639b84b52");
		leaguemenuteleport.withPersistedManualSkip("orderManual_784d7777d88d4b06");
		tradefortisblacksmith.withPersistedManualSkip("orderManual_67d62b460afa4227");
		equipmithrilspear.withPersistedManualSkip("orderManual_b7fd267650684572");
		lowalchsteelplatebody.withPersistedManualSkip("orderManual_c72a5c4e0fe647cf");
		depositfortiswestbank.withPersistedManualSkip("orderManual_3e2af9a8620347c0");
		enterguardbunker.withPersistedManualSkip("orderManual_8eaf1a19c68e4ffb");
		attackguardlevel21.withPersistedManualSkip("orderManual_31720dce1e1e4a80");
		exitguardbunker.withPersistedManualSkip("orderManual_4869db8bb5384fdd");
		enterthiefsden.withPersistedManualSkip("orderManual_1e67f54c3fe844bf");
		attackthieflevel16.withPersistedManualSkip("orderManual_79d03c088a424308");
		attackbuffalolevel9.withPersistedManualSkip("orderManual_f9b27713c6744b66");
		churndairychurn.withPersistedManualSkip("orderManual_760c8503c8444407");
		pickpotato2.withPersistedManualSkip("orderManual_51b78fd1fdfc4e61");
		cookfarmingpatchoven.withPersistedManualSkip("orderManual_3d13cc765ed5408f");
		combinebutterwithpotato.withPersistedManualSkip("orderManual_0ae0e6bdcdcf45e9");
		fillcompostbin.withPersistedManualSkip("orderManual_9d202cedf2014fa5");
		pickpocketmasterfarmer.withPersistedManualSkip("orderManual_b7cfacdc386d4986");
		buyseeddibberharminia.withPersistedManualSkip("orderManual_e4004bfc7ee64509");
		rakeallotment.withPersistedManualSkip("orderManual_f23c6ddf4cc1490a");
		plantpotatoseeds.withPersistedManualSkip("orderManual_93a6b1ac1fc340b5");
		payprotectharminia.withPersistedManualSkip("orderManual_90da9bcd975d4d32");
		attackdirewolflevel88.withPersistedManualSkip("orderManual_2a71162245c04190");
		attackscorpionlevel14.withPersistedManualSkip("orderManual_54e3a789deb14634");
		minecopperrocks.withPersistedManualSkip("orderManual_1a5aa05798474bd7");
		minetinrocks.withPersistedManualSkip("orderManual_09018eaa7a2c47bc");
		attackjaguarlevel67.withPersistedManualSkip("orderManual_e9a6caeaeb86458e");
		attackhillgiantlevel28.withPersistedManualSkip("orderManual_5d1d9b7e25da42ec");
		mineclayrocks.withPersistedManualSkip("orderManual_3a4a93088e334df4");
		mineironrocks.withPersistedManualSkip("orderManual_c5657aea431f42c1");
		minesilverrocks.withPersistedManualSkip("orderManual_63ed567d664b44b4");
		minecoalrocks.withPersistedManualSkip("orderManual_d2e95184cebb4197");
		minegoldrocks.withPersistedManualSkip("orderManual_910da6a3eb7c4196");
		minemoreiron.withPersistedManualSkip("orderManual_8f0d925356ff4dc8");
		minemithrilrocks.withPersistedManualSkip("orderManual_bbf01930a66a4aaf");
		bignetcoastfishingspot.withPersistedManualSkip("orderManual_95de887a08e942db");
		bignetcoastfishingspotcod.withPersistedManualSkip("orderManual_c09e62d9b397443e");
		buymouldsartima.withPersistedManualSkip("orderManual_8bc35baeddcf4ac6");
		softclayspawn.withPersistedManualSkip("orderManual_d54decdfe5e746f7");
		usepotterswheel.withPersistedManualSkip("orderManual_0d0672e59f0e4bc9");
		firepotteryoven.withPersistedManualSkip("orderManual_ffef209ef7614241");
		depositoillanterns.withPersistedManualSkip("orderManual_c6d823de02b04365");
		eatcookedrabbit.withPersistedManualSkip("orderManual_74ede07d53494e31");
		talktopicaria.withPersistedManualSkip("orderManual_a695d187db0d426b");
		givestewtooli.withPersistedManualSkip("orderManual_d54c4bcf9b4a4242");
		talktofriendlyforester.withPersistedManualSkip("orderManual_1a4549d428724f7b");
		buyfishfrompicaria.withPersistedManualSkip("orderManual_c6e722989c7e437c");
		tradecamtorumblacksmith.withPersistedManualSkip("orderManual_0c35a9124ae1418b");
		exitthiefsden.withPersistedManualSkip("orderManual_46cfdc5a52f243de");
		attackoryxlevel15.withPersistedManualSkip("orderManual_683c9b9854ea4329");
		thankfaux.withPersistedManualSkip("orderManual_23f118b7853b499a");


		section1Task = new ConditionalStep(this, depositoillanterns);
		section1Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_1, true, 18), openleaguesmenu.getSidebarManualSkipRequirement())), openleaguesmenu);
		section1Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_0, true, 19, "Complete the Leagues Tutorial"), completeleaguestutorial.getSidebarManualSkipRequirement())), completeleaguestutorial);
		section1Task.addStep(not(passOnceCompleted(claimfirstrelic.getSidebarManualSkipRequirement(), claimfirstrelic.getSidebarManualSkipRequirement())), claimfirstrelic);
		section1Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_2, true, 8, "Turn off your run"), togglerunenergy.getSidebarManualSkipRequirement())), togglerunenergy);
		section1Task.addStep(not(passOnceCompleted(new Conditions(LogicType.AND, new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_1, true, 22, "Pickpocket a Citizen"), new SkillRequirement(Skill.THIEVING, 5, true)), pickpocketcitizen.getSidebarManualSkipRequirement())), pickpocketcitizen);
		section1Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_3, true, 0, "Pet Renu"), petrenu.getSidebarManualSkipRequirement())), petrenu);
		section1Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_2, true, 10, "Bow near a quetzal"), bownearrenu.getSidebarManualSkipRequirement())), bownearrenu);
		section1Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_3, true, 12, "Travel using the Quetzal Transport System"), travelwithrenu.getSidebarManualSkipRequirement())), travelwithrenu);
		section1Task.addStep(not(passOnceCompleted(travelbackwithrenu.getSidebarManualSkipRequirement(), travelbackwithrenu.getSidebarManualSkipRequirement())), travelbackwithrenu);
		section1Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_2, true, 27, "Fill something up from a water pump"), usewaterpump.getSidebarManualSkipRequirement())), usewaterpump);
		section1Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_0, true, 4, "Attack a dummy"), attackdummy.getSidebarManualSkipRequirement())), attackdummy);
		section1Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_2, true, 14, "Cry near a child"), crynearachild.getSidebarManualSkipRequirement())), crynearachild);
		section1Task.addStep(not(passOnceCompleted(new Conditions(LogicType.AND, new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_2, true, 4, "Steal some bread"), cake.quantity(14).alsoCheckBank()), stealcakesfrombakersstall.getSidebarManualSkipRequirement())), stealcakesfrombakersstall);
		section1Task.addStep(not(passOnceCompleted(new Conditions(LogicType.AND, chisel.alsoCheckBank(), knife), tradeshopkeeper.getSidebarManualSkipRequirement())), tradeshopkeeper);
		section1Task.addStep(not(passOnceCompleted(new Conditions(LogicType.AND, brownApron, redCape), tradefloria.getSidebarManualSkipRequirement())), tradefloria);
		section1Task.addStep(not(passOnceCompleted(new Conditions(LogicType.AND, brownApron.equipped(), redCape.equipped()), equipapronandredcape.getSidebarManualSkipRequirement())), equipapronandredcape);
		section1Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_0, true, 28, "Defeat a Rat"), attackratlevel1.getSidebarManualSkipRequirement())), attackratlevel1);
		section1Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_3, true, 4, "Salute next to a statue of Quoatlos"), salutequoatlos.getSidebarManualSkipRequirement())), salutequoatlos);
		section1Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_0, true, 3, "Activate a prayer near an altar"), useprayernearshrineofralos.getSidebarManualSkipRequirement())), useprayernearshrineofralos);
		section1Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_3, true, 1, "Pet Xolo in Civitas"), petxolo.getSidebarManualSkipRequirement())), petxolo);
		section1Task.addStep(not(passOnceCompleted(new Conditions(LogicType.OR, new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_36, true, 24, "150 Combat Achievements"), new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_2, true, 23, "Drink from a bird bath")), drinkfrombirdbath.getSidebarManualSkipRequirement())), drinkfrombirdbath);
		section1Task.addStep(not(passOnceCompleted(new Conditions(LogicType.AND, new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_0, true, 14, "Chop Some Logs"), logs.quantity(2).alsoCheckBank()), chopdowntree.getSidebarManualSkipRequirement())), chopdowntree);
		section1Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_0, true, 6, "Burn Some Normal Logs"), burnlog.getSidebarManualSkipRequirement())), burnlog);
		section1Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_1, true, 8, "Fletch Some Arrow Shafts"), fletchlog.getSidebarManualSkipRequirement())), fletchlog);
		section1Task.addStep(not(passOnceCompleted(useeastcivitasbank.getSidebarManualSkipRequirement(), useeastcivitasbank.getSidebarManualSkipRequirement())), useeastcivitasbank);
		section1Task.addStep(not(passOnceCompleted(and(shears.alsoCheckBank(), glassblowingPipe.alsoCheckBank()), tradeartima.getSidebarManualSkipRequirement())), tradeartima);
		section1Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_3, true, 9, "Talk to a Gladiator"), talktogladiator.getSidebarManualSkipRequirement())), talktogladiator);
		section1Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_2, true, 6, "Talk to any Port master"), talktoportmaster.getSidebarManualSkipRequirement())), talktoportmaster);
		section1Task.addStep(not(passOnceCompleted(and(bucketOfSand.quantity(10), sodaAsh.quantity(10)), talktotradercrewmember.getSidebarManualSkipRequirement())), talktotradercrewmember);
		section1Task.addStep(not(passOnceCompleted(moltenGlass.quantity(10), usefurnace.getSidebarManualSkipRequirement())), usefurnace);
		section1Task.addStep(not(passOnceCompleted(and(moltenGlass.quantity(10).alsoCheckBank(), not(moltenGlass.quantity(1))), depositmoltenglass.getSidebarManualSkipRequirement())), depositmoltenglass);
		section1Task.addStep(not(passOnceCompleted(and(bucketOfSand.quantity(10).alsoCheckBank(), sodaAsh.quantity(10).alsoCheckBank(), moltenGlass.quantity(10).alsoCheckBank()), talktotradercrewmember2.getSidebarManualSkipRequirement())), talktotradercrewmember2);
		section1Task.addStep(not(passOnceCompleted(moltenGlass.quantity(20).alsoCheckBank(), usefurnace2.getSidebarManualSkipRequirement())), usefurnace2);
		section1Task.addStep(not(passOnceCompleted(new SkillRequirement(Skill.CRAFTING, 20, true), blowglassintooillanterns.getSidebarManualSkipRequirement())), blowglassintooillanterns);

		section2Task = new ConditionalStep(this, eatcookedrabbit);
		section2Task.addStep(not(passOnceCompleted(new Conditions(LogicType.OR, new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_1, true, 21, "Pick 6 wheat, 6 cabbages and 6 potatoes"), new VarbitRequirement(20372, 6)), pickcabbage.getSidebarManualSkipRequirement())), pickcabbage);
		section2Task.addStep(not(passOnceCompleted(new Conditions(LogicType.AND, new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_0, true, 25, "Defeat a Chicken"), bones), attackchickenlevel1.getSidebarManualSkipRequirement())), attackchickenlevel1);
		section2Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_3, true, 7, "Sit near a stolen cabbage"), sitwithdogs.getSidebarManualSkipRequirement())), sitwithdogs);
		section2Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_1, true, 7, "Feed a dog some bones"), feedbonestomolossus.getSidebarManualSkipRequirement())), feedbonestomolossus);
		section2Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_3, true, 3, "Pick some Sweetcorn from a Field"), picksweetcorn.getSidebarManualSkipRequirement())), picksweetcorn);
		section2Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_1, true, 2, "Eat an Onion"), pickandeatonion.getSidebarManualSkipRequirement())), pickandeatonion);
		section2Task.addStep(not(passOnceCompleted(new VarbitRequirement(20371, 6), pickwheat.getSidebarManualSkipRequirement())), pickwheat);
		section2Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_10, true, 26, "Make some Flour"), emptyflourbin.getSidebarManualSkipRequirement())), emptyflourbin);
		section2Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_1, true, 21, "Pick 6 wheat, 6 cabbages and 6 potatoes"), pickpotato.getSidebarManualSkipRequirement())), pickpotato);
		section2Task.addStep(not(passOnceCompleted(rake, tradeharminia.getSidebarManualSkipRequirement())), tradeharminia);
		section2Task.addStep(not(passOnceCompleted(new Conditions(LogicType.OR, new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_22, true, 24, "200 Collection log slots"), new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_1, true, 26, "Rake a Flower Patch")), rakeflowerpatch.getSidebarManualSkipRequirement())), rakeflowerpatch);
		section2Task.addStep(not(passOnceCompleted(bucket, emptywaterbucket.getSidebarManualSkipRequirement())), emptywaterbucket);
		section2Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_2, true, 30, "Milk a Buffalo"), milkdairybuffalo.getSidebarManualSkipRequirement())), milkdairybuffalo);
		section2Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_3, true, 6, "Shear an Alpaca"), shearalpaca.getSidebarManualSkipRequirement())), shearalpaca);
		section2Task.addStep(not(passOnceCompleted(zanarisfairyring.getSidebarManualSkipRequirement(), zanarisfairyring.getSidebarManualSkipRequirement())), zanarisfairyring);
		section2Task.addStep(not(passOnceCompleted(new Conditions(LogicType.AND, cowhide, bones), attackcowlevel2.getSidebarManualSkipRequirement())), attackcowlevel2);
		section2Task.addStep(not(passOnceCompleted(burybones.getSidebarManualSkipRequirement(), burybones.getSidebarManualSkipRequirement())), burybones);
		section2Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_3, true, 11, "Travel to Aldarin via Fairy ring"), aldarinfairyring.getSidebarManualSkipRequirement())), aldarinfairyring);
		var grapeDone = new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_13, true, 9, "Fill a Grape Barrel for the Foreman");
		section2Task.addStep(not(passOnceCompleted(or(grapeDone, new ItemRequirement("Barrel", 30037)), talktovineyardforeman.getSidebarManualSkipRequirement())), talktovineyardforeman);
		section2Task.addStep(not(passOnceCompleted(or(grapeDone, new ChatMessageRequirement("<col=06600c>Your grape barrel is now full. You should return it to the Vineyard foreman.</col>")), collectshimmeringgrapes.getSidebarManualSkipRequirement())), collectshimmeringgrapes);
		section2Task.addStep(not(passOnceCompleted(grapeDone, returntovineyardforeman.getSidebarManualSkipRequirement())), returntovineyardforeman);
		section2Task.addStep(not(passOnceCompleted(new Conditions(LogicType.AND, coins.quantity(100), chisel), withdrawaldarinbanker.getSidebarManualSkipRequirement())), withdrawaldarinbanker);
		section2Task.addStep(not(passOnceCompleted(coins.quantity(50001).alsoCheckBank(), tradetoci.getSidebarManualSkipRequirement())), tradetoci);
		section2Task.addStep(not(passOnceCompleted(cutextragems.getSidebarManualSkipRequirement(), cutextragems.getSidebarManualSkipRequirement())), cutextragems);
		section2Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_2, true, 18, "Defeat a Seagull"), attackseagulllevel2.getSidebarManualSkipRequirement())), attackseagulllevel2);
		section2Task.addStep(not(passOnceCompleted(and(tyrasHelm, bucket, rawRabbit.quantity(5)), tradechartership.getSidebarManualSkipRequirement())), tradechartership);
		section2Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_0, true, 21, "Cook something with an apron"), cookrabbitonoven.getSidebarManualSkipRequirement())), cookrabbitonoven);
		section2Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_1, true, 1), eatcookedrabbit.getSidebarManualSkipRequirement())), eatcookedrabbit);


		section3Task = new ConditionalStep(this, talktopicaria);
		section3Task.addStep(not(passOnceCompleted(talktoantonia.getSidebarManualSkipRequirement(), talktoantonia.getSidebarManualSkipRequirement())), talktoantonia);
		section3Task.addStep(not(passOnceCompleted(or(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_1, true, 11, "Light a Torch"), unlitTorch), buytorch.getSidebarManualSkipRequirement())), buytorch);
		section3Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_1, true, 11, "Light a Torch"), lighttorch.getSidebarManualSkipRequirement())), lighttorch);
		section3Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_2, true, 26, "Fill bucket of sand"), fillabucketwithsand.getSidebarManualSkipRequirement())), fillabucketwithsand);
		section3Task.addStep(not(passOnceCompleted(and(feathers.quantity(1000), bigFishingNet), talktopicaria.getSidebarManualSkipRequirement())), talktopicaria);

		section4Task = new ConditionalStep(this, givestewtooli);
		var charterDone = new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_2, true, 11);
		section4Task.addStep(not(passOnceCompleted(charterDone, chartertocivitas.getSidebarManualSkipRequirement())), chartertocivitas);
		section4Task.addStep(not(passOnceCompleted(buypineapples.getSidebarManualSkipRequirement(), buypineapples.getSidebarManualSkipRequirement())), buypineapples);
		section4Task.addStep(not(passOnceCompleted(depositpineapples.getSidebarManualSkipRequirement(), depositpineapples.getSidebarManualSkipRequirement())), depositpineapples);
		section4Task.addStep(not(passOnceCompleted(ironMace, buyironmace.getSidebarManualSkipRequirement())), buyironmace);
		section4Task.addStep(not(passOnceCompleted(ironMace.equipped(), equipironmace.getSidebarManualSkipRequirement())), getEquipironmace());
		section4Task.addStep(not(passOnceCompleted(new Conditions(LogicType.AND, new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_1, true, 17, "Obtain a Casket from Fishing"), new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_11, true, 3, "Obtain an old boot from a fishing spot"), new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_13, true, 10, "Fish a House Key")), castfishingspot.getSidebarManualSkipRequirement())), castfishingspot);
		section4Task.addStep(not(passOnceCompleted(new Conditions(LogicType.AND, jugOfWine, stew, moonLite, cupOfTea), talktobartender.getSidebarManualSkipRequirement())), talktobartender);
		section4Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_2, true, 24, "Drink some moon-lite"), drinkmoonlite.getSidebarManualSkipRequirement())), drinkmoonlite);
		section4Task.addStep(not(passOnceCompleted(drinkjugofwine.getSidebarManualSkipRequirement(), drinkjugofwine.getSidebarManualSkipRequirement())), drinkjugofwine);
		section4Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_11, true, 29, "Successfully pickpocket a Citizen 10 times in a row"), pickpocketcitizenuntilsuccess.getSidebarManualSkipRequirement())), pickpocketcitizenuntilsuccess);
		section4Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_1, true, 25, "Purchase a Player Owned House"), buypoh.getSidebarManualSkipRequirement())), buypoh);
		section4Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_1, true, 28, "Sell some silk to a silk trader"), tradesilkmerchant.getSidebarManualSkipRequirement())), tradesilkmerchant);
		section4Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_2, true, 28), givestewtooli.getSidebarManualSkipRequirement())), givestewtooli);

		section5Task = new ConditionalStep(this, talktofriendlyforester);
		section5Task.addStep(not(passOnceCompleted(traveltoauburnvale.getSidebarManualSkipRequirement(), traveltoauburnvale.getSidebarManualSkipRequirement())), traveltoauburnvale);
		section5Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_2, true, 22), drinkcupoftea.getSidebarManualSkipRequirement())), drinkcupoftea);
		section5Task.addStep(not(passOnceCompleted(depositinauburnvale.getSidebarManualSkipRequirement(), depositinauburnvale.getSidebarManualSkipRequirement())), depositinauburnvale);
		section5Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_1, true, 5), tradesebamo.getSidebarManualSkipRequirement())), tradesebamo);
		section5Task.addStep(not(passOnceCompleted(tradelunami.getSidebarManualSkipRequirement(), tradelunami.getSidebarManualSkipRequirement())), tradelunami);
		section5Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_0, true, 15, "Chop using steel axe"), chopdowndeadtree.getSidebarManualSkipRequirement())), chopdowndeadtree);
		section5Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_2, true, 7, "Turn any Logs Into a Plank"), makeplankatsawmill.getSidebarManualSkipRequirement())), makeplankatsawmill);
		var leathChapsMade = new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_0, true, 22, "Craft Leather chaps");
		section5Task.addStep(not(passOnceCompleted(or(leathChapsMade, leather), tanleatheratchouani.getSidebarManualSkipRequirement())), tanleatheratchouani);
		section5Task.addStep(not(passOnceCompleted(or(leathChapsMade, and(needle, thread)), tradeauburnvaleshopkeeper.getSidebarManualSkipRequirement())), tradeauburnvaleshopkeeper);
		section5Task.addStep(not(passOnceCompleted(leathChapsMade, craftleatherchaps.getSidebarManualSkipRequirement())), craftleatherchaps);
		section5Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_2, true, 9, "Admire some beautiful scenery"), admirebeautifullog.getSidebarManualSkipRequirement())), admirebeautifullog);
		section5Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_3, true, 8, "Step onto an Ent trail"), steponenttrail.getSidebarManualSkipRequirement())), steponenttrail);
		section5Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_1, true, 20, "Pick 6 flax"), pickflax.getSidebarManualSkipRequirement())), pickflax);
		section5Task.addStep(not(passOnceCompleted(new Conditions(LogicType.AND,
			new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_2, true, 3, "Spin a Ball of Wool"),
			new ItemRequirement("Bowstring", ItemID.BOW_STRING)
		), spinflaxandwool.getSidebarManualSkipRequirement())), spinflaxandwool);
		section5Task.addStep(not(passOnceCompleted(forestryKit.equipped(), talktofriendlyforester.getSidebarManualSkipRequirement())), talktofriendlyforester);


		section6Task = new ConditionalStep(this, buyfishfrompicaria);
		section6Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_3, true, 10, "Travel by canoe"), traveltotalteklan.getSidebarManualSkipRequirement())), traveltotalteklan);
		section6Task.addStep(not(passOnceCompleted(depositintalteklan.getSidebarManualSkipRequirement(), depositintalteklan.getSidebarManualSkipRequirement())), depositintalteklan);
		section6Task.addStep(not(passOnceCompleted(tradeteicuh.getSidebarManualSkipRequirement(), tradeteicuh.getSidebarManualSkipRequirement())), tradeteicuh);
		var dyedCapePurple = new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_1, true, 0, "Dye a cape Purple");
		section6Task.addStep(not(passOnceCompleted(new Conditions(LogicType.OR, dyedCapePurple, purpleDye), tradexochitl.getSidebarManualSkipRequirement())), tradexochitl);
		section6Task.addStep(not(passOnceCompleted(dyedCapePurple, dyecape.getSidebarManualSkipRequirement())), dyecape);
		section6Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_2, true, 15, "Dance near a bard"), dancenearabard.getSidebarManualSkipRequirement())), dancenearabard);
		section6Task.addStep(not(passOnceCompleted(new Conditions(LogicType.AND, shortbow, ironArrow.quantity(10)), tradearcuani.getSidebarManualSkipRequirement())), tradearcuani);
		section6Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_0, true, 26, "Kill frog"), attackfroglevel5.getSidebarManualSkipRequirement())), attackfroglevel5);
		section6Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_2, true, 12, "Tree in Tlati Rainforest"), chopdowndeadtreerainforest.getSidebarManualSkipRequirement())), chopdowndeadtreerainforest);
		section6Task.addStep(not(passOnceCompleted(traveltokastori.getSidebarManualSkipRequirement(), traveltokastori.getSidebarManualSkipRequirement())), traveltokastori);
		section6Task.addStep(not(passOnceCompleted(new Conditions(LogicType.AND, smallFishingNet, fishingRod, fishingBait.quantity(50), lobsterPot, flyFishingRod, harpoon), tradesulisal.getSidebarManualSkipRequirement())), tradesulisal);
		section6Task.addStep(not(passOnceCompleted(sardinefishingspot.getSidebarManualSkipRequirement(), sardinefishingspot.getSidebarManualSkipRequirement())), sardinefishingspot);
		section6Task.addStep(not(passOnceCompleted(cooksardinesatoven.getSidebarManualSkipRequirement(), cooksardinesatoven.getSidebarManualSkipRequirement())), cooksardinesatoven);
		section6Task.addStep(not(passOnceCompleted(new Conditions(LogicType.AND, new SkillRequirement(Skill.FISHING, 10, true), rawShrimps, new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_0, true, 12, "Catch a Shrimp")), shrimpfishingspot.getSidebarManualSkipRequirement())), shrimpfishingspot);
		section6Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_0, true, 20, "Cook Shrimp"), cookshrimpatoven.getSidebarManualSkipRequirement())), cookshrimpatoven);
		section6Task.addStep(not(passOnceCompleted(new SkillRequirement(Skill.FISHING, 15, true), herringfishingspot.getSidebarManualSkipRequirement())), herringfishingspot);
		section6Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_0, true, 13, "Catch an Anchovy"), anchovyfishingspot.getSidebarManualSkipRequirement())), anchovyfishingspot);
		var impBasement = new ZoneRequirement(new Zone(new WorldPoint(1375, 9449, 0), new WorldPoint(1387, 9457, 0)));
		section6Task.addStep(not(passOnceCompleted(impBasement, climbdownladder.getSidebarManualSkipRequirement())), climbdownladder);
		var killedImpInBasement = new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_2, true, 21, "Killed basement imp");
		var killedImpWithEarth = new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_0, true, 30, "Killed imp with earth spells");
		section6Task.addStep(not(passOnceCompleted(and(fiendishAshes, killedImpWithEarth, killedImpInBasement), attackimplevel2.getSidebarManualSkipRequirement())), attackimplevel2);
		section6Task.addStep(not(passOnceCompleted(not(impBasement), climbupladder.getSidebarManualSkipRequirement())), climbupladder);
		section6Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_3, true, 2, "Pet a Caique"), petcaique.getSidebarManualSkipRequirement())), petcaique);
		section6Task.addStep(not(passOnceCompleted(new VarbitRequirement(VarbitID.PENDANT_OF_ATES_TLATI_FOUND, 1), activatestatuekastori.getSidebarManualSkipRequirement())), activatestatuekastori);
		section6Task.addStep(not(passOnceCompleted(new Conditions(LogicType.AND, new SkillRequirement(Skill.ATTACK, 20, false), new SkillRequirement(Skill.MAGIC, 21, false), new SkillRequirement(Skill.DEFENCE, 5, false)), attackgemstonecrablevel160.getSidebarManualSkipRequirement())), attackgemstonecrablevel160);
		var witnessCrabDie = new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_3, true, 14, "See gemstone crab burrow");
		section6Task.addStep(not(passOnceCompleted(witnessCrabDie, witnessthegemcrabsdefeat.getSidebarManualSkipRequirement())), witnessthegemcrabsdefeat);
		section6Task.addStep(not(passOnceCompleted(travelgloomthorn.getSidebarManualSkipRequirement(), travelgloomthorn.getSidebarManualSkipRequirement())), travelgloomthorn);
		section6Task.addStep(not(passOnceCompleted(new VarbitRequirement(VarbitID.PENDANT_OF_ATES_AUBURN_FOUND, 1), activatestatuenemus.getSidebarManualSkipRequirement())), activatestatuenemus);
		section6Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_2, true, 20, "Defeat an Icefiend in Varlamore"), attackicefiendlevel13.getSidebarManualSkipRequirement())), attackicefiendlevel13);
		section6Task.addStep(not(passOnceCompleted(new VarbitRequirement(VarbitID.PENDANT_OF_ATES_DARKFROST_FOUND, 1), activatestatuedarkfrost.getSidebarManualSkipRequirement())), activatestatuedarkfrost);
		section6Task.addStep(not(passOnceCompleted(ironMace.equipped(), equipironmace2.getSidebarManualSkipRequirement())), equipironmace2);
		var killed5Bunnies = new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_8, true, 29, "Killed 5 bunnies");
		var killed5ThingsWithMace = new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_8, true, 30, "Killed 5 with mace");
		section6Task.addStep(not(passOnceCompleted(and(killed5Bunnies, killed5ThingsWithMace), attackrabbitlevel2.getSidebarManualSkipRequirement())), attackrabbitlevel2);
		section6Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_0, true, 9, "Cast Home Teleport"), hometeleport.getSidebarManualSkipRequirement())), hometeleport);
		var agility666 = new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_13, true, 14, "666 jumps");
		section6Task.addStep(not(passOnceCompleted(agility666, yamaagility.getSidebarManualSkipRequirement())), yamaagility);
		section6Task.addStep(not(passOnceCompleted(not(new ZoneRequirement(new Zone(new WorldPoint(1463, 5566, 0), new WorldPoint(1546, 5644, 0)))), returntocivitas.getSidebarManualSkipRequirement())), returntocivitas);
		section6Task.addStep(not(passOnceCompleted(new ZoneRequirement(new Zone(new WorldPoint(1277, 2834, 0), new WorldPoint(1492, 3002, 0))), travelaldarin.getSidebarManualSkipRequirement())), travelaldarin);
		section6Task.addStep(not(passOnceCompleted(tyrasHelm.equipped(), withdrawtyrashelm.getSidebarManualSkipRequirement())), withdrawtyrashelm);
		section6Task.addStep(not(passOnceCompleted(new Conditions(LogicType.AND, chisel, coins), withdrawaldarinbanker2.getSidebarManualSkipRequirement())), withdrawaldarinbanker2);
		section6Task.addStep(not(passOnceCompleted(new ObjectCondition(15314) /* Door make object */, enterportal.getSidebarManualSkipRequirement())), enterportal);
		var builtRoom = new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_7, true, 10);
		section6Task.addStep(not(passOnceCompleted(builtRoom, buildroom.getSidebarManualSkipRequirement())), buildroom);
		section6Task.addStep(not(passOnceCompleted(not(new ObjectCondition(15314)) /* Door make object */, exitportal.getSidebarManualSkipRequirement())), exitportal);
		section6Task.addStep(not(passOnceCompleted(new SkillRequirement(Skill.CRAFTING, 70, false), tradetociagain.getSidebarManualSkipRequirement())), tradetociagain);
		section6Task.addStep(not(passOnceCompleted(not(new ZoneRequirement(new Zone(new WorldPoint(1277, 2834, 0), new WorldPoint(1492, 3002, 0)))), talktoantonia2.getSidebarManualSkipRequirement())), talktoantonia2);
		section6Task.addStep(not(passOnceCompleted(and(new ItemRequirement("Bronze full helm", ItemID.BRONZE_FULL_HELM), new ItemRequirement("Adamant full helm", ItemID.ADAMANT_FULL_HELM)), tradethurid.getSidebarManualSkipRequirement())), tradethurid);
		section6Task.addStep(not(passOnceCompleted(and(
			new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_8, true, 20), /*sworfish*/
			new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_8, true, 21) /*tuna*/
		), buyfishfrompicaria.getSidebarManualSkipRequirement())), buyfishfrompicaria);

		var inCamTorum = new ZoneRequirement(new Zone(new WorldPoint(1378, 9502, 1), new WorldPoint(1524, 9600, 3)));
		section7Task = new ConditionalStep(this, tradecamtorumblacksmith);
		section7Task.addStep(not(passOnceCompleted(inCamTorum, entercamtorum.getSidebarManualSkipRequirement())), entercamtorum);
		section7Task.addStep(not(passOnceCompleted(
			new Conditions(LogicType.AND, new ItemRequirement("Eye of newt", 222).quantity(100),
				new ItemRequirement("Vial of water", 228).quantity(100), pestleAndMortar), tradehuito.getSidebarManualSkipRequirement())), tradehuito);
		var steelPick = new ItemRequirement("Steel pickaxe", ItemID.STEEL_PICKAXE);
		var mithPick = new ItemRequirement("Mithril pickaxe", ItemID.MITHRIL_PICKAXE);
		var addyPick = new ItemRequirement("Adamant pickaxe", ItemID.ADAMANT_PICKAXE);
		var runePick = new ItemRequirement("Rune pickaxe", ItemID.RUNE_PICKAXE);
		section7Task.addStep(not(passOnceCompleted(and(steelPick, mithPick, addyPick, runePick), tradetizoro.getSidebarManualSkipRequirement())), tradetizoro);
		var hadHaircut = new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_1, true, 10);
		section7Task.addStep(not(passOnceCompleted(hadHaircut, haircutsonalo.getSidebarManualSkipRequirement())), haircutsonalo);
		var hadBeardTrimmed = new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_3, true, 13);
		section7Task.addStep(not(passOnceCompleted(hadBeardTrimmed, shavesonalo.getSidebarManualSkipRequirement())), shavesonalo);
		var lawRunes = new ItemRequirement("Law runes", ItemID.LAWRUNE);
		var natureRunes = new ItemRequirement("Nature runes", ItemID.NATURERUNE);
		var deathRunes = new ItemRequirement("Death runes", ItemID.DEATHRUNE);
		var gotRunes = and(lawRunes.quantity(50), natureRunes.quantity(500), deathRunes.quantity(100));
		section7Task.addStep(not(passOnceCompleted(gotRunes, tradenahta.getSidebarManualSkipRequirement())), tradenahta);
		var castLowAlch = new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_7, true, 17);
		section7Task.addStep(not(passOnceCompleted(castLowAlch, lowalchrunes.getSidebarManualSkipRequirement())), lowalchrunes);
		section7Task.addStep(not(passOnceCompleted(bigFishingNet, depositcamtorumbanker.getSidebarManualSkipRequirement())), depositcamtorumbanker);
		section7Task.addStep(not(passOnceCompleted(jugOfWine.quantity(5), tradecamtorumbartender.getSidebarManualSkipRequirement())), tradecamtorumbartender);
		var equipIronDagger = new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_1, true, 6);
		section7Task.addStep(not(passOnceCompleted(equipIronDagger, tradecamtorumblacksmith.getSidebarManualSkipRequirement())), tradecamtorumblacksmith);

		section8Task = new ConditionalStep(this, exitthiefsden);
		section8Task.addStep(not(passOnceCompleted(not(inCamTorum), leaguemenuteleport.getSidebarManualSkipRequirement())), leaguemenuteleport);
		var equippedMithWeapon = new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_9, true, 8);
		section8Task.addStep(not(passOnceCompleted(new Conditions(LogicType.AND, adamantPlatebody, adamantPlatelegs, bronzePlatelegs, bronzePlatebody, mithrilSpear, steelPlatebody), tradefortisblacksmith.getSidebarManualSkipRequirement())), tradefortisblacksmith);
		section8Task.addStep(not(passOnceCompleted(equippedMithWeapon, equipmithrilspear.getSidebarManualSkipRequirement())), equipmithrilspear);
		var lowAlched500gp = new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_8, true, 19);
		section8Task.addStep(not(passOnceCompleted(lowAlched500gp, lowalchsteelplatebody.getSidebarManualSkipRequirement())), lowalchsteelplatebody);
		section8Task.addStep(not(passOnceCompleted(depositfortiswestbank.getSidebarManualSkipRequirement(), depositfortiswestbank.getSidebarManualSkipRequirement())), depositfortiswestbank);
		var inGuardBasement = new ZoneRequirement(new Zone(new WorldPoint(1616, 9556, 0), new WorldPoint(1667, 9587, 0)));
		var killedGuard = new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_2, true, 17);
		section8Task.addStep(not(passOnceCompleted(or(inGuardBasement, killedGuard), enterguardbunker.getSidebarManualSkipRequirement())), enterguardbunker);
		section8Task.addStep(not(passOnceCompleted(killedGuard, attackguardlevel21.getSidebarManualSkipRequirement())), attackguardlevel21);
		section8Task.addStep(not(passOnceCompleted(not(inGuardBasement), exitguardbunker.getSidebarManualSkipRequirement())), exitguardbunker);
		var inThiefDen = new ZoneRequirement(new Zone(new WorldPoint(1608, 9560, 0), new WorldPoint(1625, 9579, 0)));
		var killedThief = new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_2, true, 19);
		var exitDen = new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_2, true, 25);
		section8Task.addStep(not(passOnceCompleted(or(inThiefDen, and(killedThief, exitDen)), enterthiefsden.getSidebarManualSkipRequirement())), enterthiefsden);
		section8Task.addStep(not(passOnceCompleted(killedThief, attackthieflevel16.getSidebarManualSkipRequirement())), attackthieflevel16);
		section8Task.addStep(not(passOnceCompleted(exitDen, exitthiefsden.getSidebarManualSkipRequirement())), exitthiefsden);

		section9Task = new ConditionalStep(this, attackoryxlevel15);
		var churnedButter = new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_8, true, 3, "Churn some butter");
		section9Task.addStep(not(passOnceCompleted(or(bucketOfMilk, churnedButter), milkdairybuffalo2.getSidebarManualSkipRequirement())), milkdairybuffalo2);
		section9Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_2, true, 16, "Defeat a Buffalo"), attackbuffalolevel9.getSidebarManualSkipRequirement())), attackbuffalolevel9);
		section9Task.addStep(not(passOnceCompleted(churnedButter, churndairychurn.getSidebarManualSkipRequirement())), churndairychurn);
		var madeButterPotato = new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_7, true, 16, "Butter a potato");
		section9Task.addStep(not(passOnceCompleted(or(potato, madeButterPotato), pickpotato2.getSidebarManualSkipRequirement())), pickpotato2);
		section9Task.addStep(not(passOnceCompleted(or(bakedPotato, madeButterPotato), cookfarmingpatchoven.getSidebarManualSkipRequirement())), cookfarmingpatchoven);
		section9Task.addStep(not(passOnceCompleted(madeButterPotato, combinebutterwithpotato.getSidebarManualSkipRequirement())), combinebutterwithpotato);
		var compostFull = new VarbitRequirement(VarbitID.FARMING_TRANSMIT_E2, 5, Operation.GREATER_EQUAL);
		section9Task.addStep(not(passOnceCompleted(compostFull, fillcompostbin.getSidebarManualSkipRequirement())), fillcompostbin);
		var plantedPotatoSeeds = new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_1, true, 23, "Plant Seeds in an Allotment Patch");
		section9Task.addStep(not(passOnceCompleted(or(plantedPotatoSeeds, potatoSeed.quantity(3)), pickpocketmasterfarmer.getSidebarManualSkipRequirement())), pickpocketmasterfarmer);
		var protectedCrops = new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_1, true, 24, "Protect Your Crops");
		section9Task.addStep(not(passOnceCompleted(or(protectedCrops, new Conditions(LogicType.AND, rake, seedDibber, compost.quantity(2))), buyseeddibberharminia.getSidebarManualSkipRequirement())), buyseeddibberharminia);
		section9Task.addStep(not(passOnceCompleted(or(new VarbitRequirement(VarbitID.FARMING_TRANSMIT_B1, 3, Operation.GREATER_EQUAL), plantedPotatoSeeds), rakeallotment.getSidebarManualSkipRequirement())), rakeallotment);
		section9Task.addStep(not(passOnceCompleted(plantedPotatoSeeds, plantpotatoseeds.getSidebarManualSkipRequirement())), plantpotatoseeds);
		section9Task.addStep(not(passOnceCompleted(protectedCrops, payprotectharminia.getSidebarManualSkipRequirement())), payprotectharminia);
		section9Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_12, true, 24, "Defeat a Dire Wolf"), attackdirewolflevel88.getSidebarManualSkipRequirement())), attackdirewolflevel88);
		section9Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_9, true, 0, "Defeat a Scorpion with a Mithril Spear"), attackscorpionlevel14.getSidebarManualSkipRequirement())), attackscorpionlevel14);
		section9Task.addStep(not(passOnceCompleted(copperOre, minecopperrocks.getSidebarManualSkipRequirement())), minecopperrocks);

		section9Task.addStep(not(passOnceCompleted(new Conditions(LogicType.AND, new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_1, true, 13, "Mine 5 Tin Ore"), new SkillRequirement(Skill.MINING, 15, true)), minetinrocks.getSidebarManualSkipRequirement())), minetinrocks);
		section9Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_12, true, 25, null), attackjaguarlevel67.getSidebarManualSkipRequirement())), attackjaguarlevel67);
		section9Task.addStep(not(passOnceCompleted(new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_0, true, 27, "Defeat a Hill Giant"), attackhillgiantlevel28.getSidebarManualSkipRequirement())), attackhillgiantlevel28);
		var minedClay = new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_1, true, 14);
		section9Task.addStep(not(passOnceCompleted(minedClay, mineclayrocks.getSidebarManualSkipRequirement())), mineclayrocks);
		var minedIron = new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_10, true, 28);
		section9Task.addStep(not(passOnceCompleted(minedIron, mineironrocks.getSidebarManualSkipRequirement())), mineironrocks);
		var mineSilver = new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_10, true, 27);
		section9Task.addStep(not(passOnceCompleted(mineSilver, minesilverrocks.getSidebarManualSkipRequirement())), minesilverrocks);
		var minedCoal = new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_2, true, 31);
		section9Task.addStep(not(passOnceCompleted(minedCoal, minecoalrocks.getSidebarManualSkipRequirement())), minecoalrocks);
		section9Task.addStep(not(passOnceCompleted(minegoldrocks.getSidebarManualSkipRequirement(), minegoldrocks.getSidebarManualSkipRequirement())), minegoldrocks);
		section9Task.addStep(not(passOnceCompleted(new SkillRequirement(Skill.MINING, 55), minemoreiron.getSidebarManualSkipRequirement())), minemoreiron);
		var minedMithril = new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_10, true, 29);
		section9Task.addStep(not(passOnceCompleted(minedMithril, minemithrilrocks.getSidebarManualSkipRequirement())), minemithrilrocks);
		var fishedMackrel = new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_7, true, 22);
		section9Task.addStep(not(passOnceCompleted(fishedMackrel, bignetcoastfishingspot.getSidebarManualSkipRequirement())), bignetcoastfishingspot);
		var fishedCod = new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_7, true, 19);
		section9Task.addStep(not(passOnceCompleted(fishedCod, bignetcoastfishingspotcod.getSidebarManualSkipRequirement())), bignetcoastfishingspotcod);

		var killedOryx = new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_12, true, 26);
		section9Task.addStep(not(passOnceCompleted(killedOryx, attackoryxlevel15.getSidebarManualSkipRequirement())), attackoryxlevel15);


		var tiaraMould = new ItemRequirement("Tiara mould", ItemID.TIARA_MOULD);
		var ringMould = new ItemRequirement("Ring mould", ItemID.RING_MOULD);
		var amuletMould = new ItemRequirement("Amulet mould", ItemID.AMULET_MOULD);
		section10Task = new ConditionalStep(this, thankfaux);
		var madePottery = new VarplayerRequirement(VarPlayerID.LEAGUE_TASK_COMPLETED_13, true, 28);
		section10Task.addStep(not(passOnceCompleted(or(madePottery, and(tiaraMould.alsoCheckBank(), ringMould.alsoCheckBank(), amuletMould.alsoCheckBank())), buymouldsartima.getSidebarManualSkipRequirement())), buymouldsartima);
		section10Task.addStep(not(passOnceCompleted(or(madePottery, softClay), softclayspawn.getSidebarManualSkipRequirement())), softclayspawn);
		var unfiredItem = new ItemRequirement("Unfired item", ItemID.BOWL_UNFIRED);
		unfiredItem.addAlternates(ItemID.CUP_UNFIRED, ItemID.PIEDISH_UNFIRED, ItemID.POT_UNFIRED, ItemID.PLANTPOT_UNFIRED);
		section10Task.addStep(not(passOnceCompleted(or(unfiredItem, madePottery), usepotterswheel.getSidebarManualSkipRequirement())), usepotterswheel);
		section10Task.addStep(not(passOnceCompleted(madePottery, firepotteryoven.getSidebarManualSkipRequirement())), firepotteryoven);

		ConditionalStep allSections = new ConditionalStep(this, section10Task);
		allSections.addStep(section1Task);
		allSections.addStep(section2Task);
		allSections.addStep(section3Task);
		allSections.addStep(section4Task);
		allSections.addStep(section5Task);
		allSections.addStep(section6Task);
		allSections.addStep(section7Task);
		allSections.addStep(section8Task);
		allSections.addStep(section9Task);

		return allSections;

		// TODO: refine per-step requirement logic for real route progression conditions.
	}

	private DetailedQuestStep getEquipironmace()
	{
		return equipironmace;
	}

	private Requirement passOnceCompleted(Requirement completion, ManualRequirement manualOverride)
	{
		if (completion == null || manualOverride == null)
		{
			return completion;
		}
		return new Requirement()
		{
			@Override
			public boolean check(Client client)
			{
				if (manualOverride.check(client))
				{
					return true;
				}
				boolean passed = completion.check(client);
				if (passed && !manualOverride.check(client))
				{
					manualOverride.setShouldPass(true);
				}
				return passed;
			}

			@Override
			public String getDisplayText()
			{
				return completion.getDisplayText();
			}
		};
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails section1Steps = new PanelDetails("Civitas illa Fortis", List.of(openleaguesmenu, completeleaguestutorial, claimfirstrelic, togglerunenergy, pickpocketcitizen, petrenu, bownearrenu, travelwithrenu, travelbackwithrenu, usewaterpump, attackdummy, crynearachild, stealcakesfrombakersstall, tradeshopkeeper, tradefloria, equipapronandredcape, attackratlevel1, salutequoatlos, useprayernearshrineofralos, petxolo, drinkfrombirdbath, chopdowntree, burnlog, fletchlog, useeastcivitasbank, tradeartima, talktogladiator, talktoportmaster, talktotradercrewmember, usefurnace, depositmoltenglass, talktotradercrewmember2, usefurnace2, blowglassintooillanterns, depositoillanterns));
		section1Steps.setLockingStep(section1Task);
		allSteps.add(section1Steps);

		PanelDetails section2Steps = new PanelDetails("Outer Fortis", List.of(pickcabbage, attackchickenlevel1, sitwithdogs, feedbonestomolossus, picksweetcorn, pickandeatonion, pickwheat, emptyflourbin, pickpotato, tradeharminia, rakeflowerpatch, emptywaterbucket, milkdairybuffalo, shearalpaca, zanarisfairyring, attackcowlevel2, burybones, aldarinfairyring, talktovineyardforeman, collectshimmeringgrapes, returntovineyardforeman, withdrawaldarinbanker, tradetoci, cutextragems, attackseagulllevel2, tradechartership, cookrabbitonoven, eatcookedrabbit));
		section2Steps.setLockingStep(section2Task);
		allSteps.add(section2Steps);

		PanelDetails section3Steps = new PanelDetails("Sunset Coast", List.of(talktoantonia, buytorch, lighttorch, fillabucketwithsand, talktopicaria));
		section3Steps.setLockingStep(section3Task);
		allSteps.add(section3Steps);

		PanelDetails section4Steps = new PanelDetails("Civitas illa Fortis", List.of(chartertocivitas, buypineapples, depositpineapples, buyironmace, equipironmace, castfishingspot, talktobartender, drinkmoonlite, drinkjugofwine, pickpocketcitizenuntilsuccess, buypoh, tradesilkmerchant, givestewtooli));
		section4Steps.setLockingStep(section4Task);
		allSteps.add(section4Steps);

		PanelDetails section5Steps = new PanelDetails("Auburnvale", List.of(traveltoauburnvale, drinkcupoftea, depositinauburnvale, tradesebamo, tradelunami, chopdowndeadtree, makeplankatsawmill, tanleatheratchouani, tradeauburnvaleshopkeeper, craftleatherchaps, admirebeautifullog, steponenttrail, pickflax, spinflaxandwool, talktofriendlyforester));
		section5Steps.setLockingStep(section5Task);
		allSteps.add(section5Steps);

		PanelDetails section6Steps = new PanelDetails("Tal Teklan", List.of(traveltotalteklan, depositintalteklan, tradeteicuh, tradexochitl, dyecape, dancenearabard, tradearcuani, attackfroglevel5, chopdowndeadtreerainforest, traveltokastori, tradesulisal, sardinefishingspot, cooksardinesatoven, shrimpfishingspot, cookshrimpatoven, herringfishingspot, anchovyfishingspot, climbdownladder, attackimplevel2, climbupladder, petcaique, activatestatuekastori, attackgemstonecrablevel160, witnessthegemcrabsdefeat, travelgloomthorn, activatestatuenemus, attackicefiendlevel13, activatestatuedarkfrost, equipironmace2, attackrabbitlevel2, hometeleport, yamaagility, returntocivitas, travelaldarin, withdrawtyrashelm, withdrawaldarinbanker2, enterportal, buildroom, exitportal, tradetociagain, talktoantonia2, tradethurid, buyfishfrompicaria));
		section6Steps.setLockingStep(section6Task);
		allSteps.add(section6Steps);

		PanelDetails section7Steps = new PanelDetails("Cam Torum", List.of(entercamtorum, tradehuito, tradetizoro, haircutsonalo, shavesonalo, tradenahta, lowalchrunes, depositcamtorumbanker, tradecamtorumbartender, tradecamtorumblacksmith));
		section7Steps.setLockingStep(section7Task);
		allSteps.add(section7Steps);

		PanelDetails section8Steps = new PanelDetails("Civitas illa Fortis", List.of(leaguemenuteleport, tradefortisblacksmith, equipmithrilspear, lowalchsteelplatebody, depositfortiswestbank, enterguardbunker, attackguardlevel21, exitguardbunker, enterthiefsden, attackthieflevel16, exitthiefsden));
		section8Steps.setLockingStep(section8Task);
		allSteps.add(section8Steps);

		PanelDetails section9Steps = new PanelDetails("Varlamore", List.of(milkdairybuffalo2, attackbuffalolevel9, churndairychurn, pickpotato2, cookfarmingpatchoven, combinebutterwithpotato, fillcompostbin, pickpocketmasterfarmer, buyseeddibberharminia, rakeallotment, plantpotatoseeds, payprotectharminia, attackdirewolflevel88, attackscorpionlevel14, minecopperrocks, minetinrocks, attackjaguarlevel67, attackhillgiantlevel28, mineclayrocks, mineironrocks, minesilverrocks, minecoalrocks, minegoldrocks, minemoreiron, minemithrilrocks, bignetcoastfishingspot, bignetcoastfishingspotcod, attackoryxlevel15));
		section9Steps.setLockingStep(section9Task);
		allSteps.add(section9Steps);

		PanelDetails section10Steps = new PanelDetails("Civitas illa Fortis", List.of(buymouldsartima, softclayspawn, usepotterswheel, firepotteryoven, thankfaux));
		section10Steps.setLockingStep(section10Task);
		allSteps.add(section10Steps);

		return allSteps;
	}
}

/*
 Build warnings:
 - Missing item requirement var for tree leaf raw id 1735
 - Missing item requirement var for tree leaf raw id 1785
 - Missing item requirement var for tree leaf raw id 1781
 - Missing item requirement var for tree leaf raw id 1775
 - Missing item requirement var for tree leaf raw id 4525
 - Missing item requirement var for tree leaf raw id 1965
 - Missing item requirement var for tree leaf raw id 1777
 - Missing item requirement var for tree leaf raw id 1735
 - Missing item requirement var for tree leaf raw id 1785
 - Missing item requirement var for tree leaf raw id 1781
 - Missing item requirement var for tree leaf raw id 1775
 - Missing item requirement var for tree leaf raw id 4525
 - Missing item requirement var for tree leaf raw id 1965
 - Missing item requirement var for tree leaf raw id 314
 - Missing item requirement var for tree leaf raw id 1777
*/

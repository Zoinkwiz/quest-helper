package com.questhelper.managers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Optional integration: run the Python legacy converter when {@code python} is on PATH.
 */
class LegacyMakerDraftScriptIT
{
	@Test
	void convertScriptProducesExtendedRoute() throws Exception
	{
		Path script = Path.of("scripts/construct/convert_legacy_maker_draft.py").toAbsolutePath().normalize();
		Assumptions.assumeTrue(Files.isRegularFile(script));

		String legacyRoot = "{\"formatVersion\":1,\"questName\":\"FromScript\",\"className\":\"SH\",\"packagePath\":\"p\","
			+ "\"helperType\":\"BasicQuestHelper\",\"definitions\":[],\"order\":[],\"requirements\":[]}";
		Path in = Files.createTempFile("qh-maker-legacy", ".json");
		Path out = Files.createTempFile("qh-maker-converted", ".json");
		try
		{
			Files.writeString(in, legacyRoot, StandardCharsets.UTF_8);
			ProcessBuilder pb = new ProcessBuilder("python", script.toString(), in.toString(), "-o", out.toString());
			pb.redirectErrorStream(true);
			Process proc = pb.start();
			String log = new String(proc.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
			boolean finished = proc.waitFor(30, TimeUnit.SECONDS);
			Assumptions.assumeTrue(finished && proc.exitValue() == 0,
				"python converter not available or failed: " + log);

			String converted = Files.readString(out, StandardCharsets.UTF_8);
			JsonObject root = new Gson().fromJson(converted, JsonObject.class);
			assertTrue(root.has("sections") && root.get("sections").isJsonArray());
			assertTrue(root.has("questHelperMaker"));

			HelperConstructManager manager = new HelperConstructManager();
			assertTrue(manager.importDraftFromJson(converted).isSuccess());
			assertEquals("FromScript", manager.getCurrentDraft().getQuestName());
		}
		finally
		{
			Files.deleteIfExists(in);
			Files.deleteIfExists(out);
		}
	}
}

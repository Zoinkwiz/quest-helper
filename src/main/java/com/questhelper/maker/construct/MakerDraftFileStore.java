package com.questhelper.maker.construct;

import net.runelite.client.RuneLite;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Location and raw I/O for the auto-saved Quest Helper Maker draft file under the RuneLite profile.
 */
public final class MakerDraftFileStore
{
	private static final String DRAFT_SUBDIR = "quest-helper";
	private static final String DRAFT_FILENAME = "construct-draft.json";

	private MakerDraftFileStore()
	{
	}

	public static File draftFileOrNull()
	{
		if (RuneLite.RUNELITE_DIR == null)
		{
			return null;
		}
		return new File(new File(RuneLite.RUNELITE_DIR, DRAFT_SUBDIR), DRAFT_FILENAME);
	}

	public static void writeUtf8(File file, String text) throws IOException
	{
		if (file == null)
		{
			return;
		}
		File parent = file.getParentFile();
		if (parent != null)
		{
			Files.createDirectories(parent.toPath());
		}
		Files.writeString(file.toPath(), text, StandardCharsets.UTF_8);
	}
}

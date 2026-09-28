/*
 * Copyright (c) 2026, Zoinkwiz <https://github.com/Zoinkwiz>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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

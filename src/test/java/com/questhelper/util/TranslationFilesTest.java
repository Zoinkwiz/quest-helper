package com.questhelper.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

// Guards the translation files against formatting mistakes that would make
// entries silently unreachable at runtime.
public class TranslationFilesTest
{
	private static final Path TRANSLATIONS = Path.of("src/main/resources/translations");

	@Test
	public void translationFilesAreWellFormed() throws IOException
	{
		try (Stream<Path> files = Files.walk(TRANSLATIONS))
		{
			List<Path> propertiesFiles = files.filter(path -> path.toString().endsWith(".properties"))
				.collect(Collectors.toList());
			assertFalse(propertiesFiles.isEmpty(), "No translation files found");

			for (Path file : propertiesFiles)
			{
				assertTrue(file.getFileName().toString().matches("[a-z0-9_]+\\.properties"),
					"Unexpected translation file name: " + file);

				Set<String> keys = new HashSet<>();
				for (String line : Files.readAllLines(file, StandardCharsets.UTF_8))
				{
					if (line.isBlank() || line.startsWith("#") || line.startsWith("!"))
					{
						continue;
					}

					int separator = indexOfUnescapedEquals(line);
					assertTrue(separator > 0, "Missing '=' in " + file + ": " + line);
					assertTrue(keys.add(line.substring(0, separator)), "Duplicate key in " + file + ": " + line);
					assertFalse(line.substring(separator + 1).isBlank(), "Empty translation in " + file + ": " + line);
				}

				assertFalse(keys.isEmpty(), "No entries in " + file);
			}
		}
	}

	private static int indexOfUnescapedEquals(String line)
	{
		boolean escaped = false;
		for (int i = 0; i < line.length(); i++)
		{
			char c = line.charAt(i);
			if (escaped)
			{
				escaped = false;
			}
			else if (c == '\\')
			{
				escaped = true;
			}
			else if (c == '=')
			{
				return i;
			}
		}
		return -1;
	}
}

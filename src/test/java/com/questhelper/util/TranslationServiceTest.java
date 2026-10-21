package com.questhelper.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

// Tests for TranslationService: native language passes through with no files,
// other languages resolve per-quest files, missing entries fall back to the
// English input text itself.
public class TranslationServiceTest
{
	// English is native: no namespace and no files, input passes through unchanged.
	@Test
	public void englishModePassesInputThrough()
	{
		var service = new TranslationService();
		assertEquals("Get a bucket", service.t("cooks_assistant", "Get a bucket"));
	}

	// Native language ignores any namespace.
	@Test
	public void englishModeIgnoresNamespace()
	{
		var service = new TranslationService();
		assertEquals("Starting off", service.t("cooks_assistant", "Starting off"));
	}

	// Spanish resolves the quest's own file.
	@Test
	public void spanishModeResolvesTranslatedQuestString()
	{
		var service = new TranslationService();
		service.setLanguage(Language.SPANISH);
		assertEquals("Compra un Bucket en la General Store de Lumbridge.",
			service.t("cooks_assistant", "Purchase a bucket from the Lumbridge General Store."));
	}

	// The same key in a different quest's file resolves from that file.
	@Test
	public void spanishModeResolvesPerQuestNamespace()
	{
		var service = new TranslationService();
		service.setLanguage(Language.SPANISH);
		assertEquals("Empezando", service.t("cooks_assistant", "Starting off"));
		assertEquals("Empezando", service.t("rune_mysteries", "Starting off"));
	}

	// An untranslated quest string falls back to the English input text itself,
	// never to a raw internal key.
	@Test
	public void spanishModeFallsBackToEnglishTextNotAKey()
	{
		var service = new TranslationService();
		service.setLanguage(Language.SPANISH);
		assertEquals("A future quest step not yet translated",
			service.t("cooks_assistant", "A future quest step not yet translated"));
	}

	// A missing file for a quest with no translations falls back to the input.
	@Test
	public void missingNamespaceFileFallsBackToInputText()
	{
		var service = new TranslationService();
		service.setLanguage(Language.SPANISH);
		assertEquals("Some string of a quest not yet translated",
			service.t("dragon_slayer_i", "Some string of a quest not yet translated"));
	}

	// Null namespace returns the native string even in Spanish mode.
	@Test
	public void nullNamespaceReturnsNativeString()
	{
		var service = new TranslationService();
		service.setLanguage(Language.SPANISH);
		assertEquals("Cook's Assistant", service.t(null, "Cook's Assistant"));
	}

	// Null text is a programming error, caught by the assert.
	@Test
	public void nullTextThrows()
	{
		var service = new TranslationService();
		assertThrows(AssertionError.class, () -> service.t("cooks_assistant", null));
	}

	// Keys can contain '=' and newlines thanks to escaping.
	@Test
	public void escapedKeysAndValuesAreResolved()
	{
		var service = new TranslationService();
		service.setLanguage(Language.SPANISH);
		assertEquals("Valor con = iguales", service.t("escaping_test", "Key with = equals"));
		assertEquals("Primera línea\nSegunda línea", service.t("escaping_test", "Line one\nLine two"));
		assertEquals("Barra invertida", service.t("escaping_test", "Back\\slash"));
	}

	// Changing language at runtime takes effect immediately on the next call.
	@Test
	public void languageChangeAppliesImmediately()
	{
		var service = new TranslationService();
		assertEquals("Starting off", service.t("cooks_assistant", "Starting off"));
		service.setLanguage(Language.SPANISH);
		assertEquals("Empezando", service.t("cooks_assistant", "Starting off"));
		service.setLanguage(Language.ENGLISH);
		assertEquals("Starting off", service.t("cooks_assistant", "Starting off"));
	}
}

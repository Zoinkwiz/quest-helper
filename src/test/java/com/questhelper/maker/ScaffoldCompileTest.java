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
package com.questhelper.maker;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Generates Java source from a fixture {@link HelperConstructModels.DraftHelper} and runs
 * {@code javac} on it in-process against the project test classpath, asserting it compiles
 * cleanly. Catches missing imports, method-signature drift, and the kind of "scaffolding emits
 * code that no longer compiles" bug that the build alone can't catch (since generated source
 * isn't part of the regular build).
 */
class ScaffoldCompileTest
{
	@Test
	void smallDraftScaffoldCompiles() throws IOException
	{
		assertScaffoldCompiles(MakerFixtures.smallDraft(), "TestGeneratedHelper");
	}

	@Test
	void comprehensiveDraftScaffoldCompiles() throws IOException
	{
		assertScaffoldCompiles(MakerFixtures.comprehensiveDraft(), "ComprehensiveGeneratedHelper");
	}

	@Test
	void scaffoldWiresSidebarManualSkipForEveryRow()
	{
		// Every non-section row should get its ManualRequirement wired as the step's sidebar skip requirement
		// and its slot id as the persistence key. Catches regressions if the PR #2693 alignment is removed.
		String src = new HelperScaffoldGenerator().generate(MakerFixtures.comprehensiveDraft()).getSource();
		assertTrue(src.contains(".setSidebarManualSkipRequirement(orderManual_"),
			"generated helper must wire sidebarManualSkipRequirement on order steps");
		assertTrue(src.contains(".setSidebarManualSkipPersistenceKey(\"slot-item-tree\")"),
			"generated helper must use the row's orderSlotId as the persistence key");
		assertTrue(src.contains("protected void onManualSidebarSkipsPersistedChanged()"),
			"generated helper must override onManualSidebarSkipsPersistedChanged so manual flips refresh the active branch");
		assertTrue(src.contains("refreshAfterRequirementChangeDeep()"),
			"override must call refreshAfterRequirementChangeDeep on the active ConditionalStep");
	}

	@Test
	void sharedStepDraftEmitsPerRowInstances() throws IOException
	{
		// Two rows share one DraftStep. Each must get its own field, its own constructor call, and its own
		// sidebar wiring — otherwise sidebar checkboxes alias and the user can't latch each occurrence.
		HelperScaffoldGenerator.GeneratedScaffold scaffold = new HelperScaffoldGenerator()
			.generate(MakerFixtures.sharedStepDraft());
		String src = scaffold.getSource();
		assertTrue(src.contains("ObjectStep usefurnace;") && src.contains("ObjectStep usefurnace2;"),
			"both rows referencing the same DraftStep must get distinct field names");
		long ctorCount = src.lines().filter(l -> l.contains("= new ObjectStep(this, 17385")).count();
		assertEquals(2L, ctorCount, "each row needs its own ObjectStep instantiation");
		assertTrue(src.contains("usefurnace.setSidebarManualSkipPersistenceKey(\"slot-furnace-first\")")
				&& src.contains("usefurnace2.setSidebarManualSkipPersistenceKey(\"slot-furnace-second\")"),
			"each row's persistence key must be wired to its own step instance");
		assertScaffoldCompiles(MakerFixtures.sharedStepDraft(), "SharedStepHelper");
	}

	private static void assertScaffoldCompiles(HelperConstructModels.DraftHelper draft, String expectedClassName) throws IOException
	{
		HelperScaffoldGenerator.GeneratedScaffold scaffold = new HelperScaffoldGenerator().generate(draft);

		assertNotNull(scaffold.getSource(), "scaffold should produce source");
		assertTrue(scaffold.getSource().contains("class " + expectedClassName),
			"generated class name should match draft");

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		assertNotNull(compiler, "JDK with javac required to run this test (ToolProvider returned null)");

		Path outDir = Files.createTempDirectory("scaffold-compile-");
		try
		{
			DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
			try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null))
			{
				fileManager.setLocation(javax.tools.StandardLocation.CLASS_OUTPUT, Collections.singletonList(outDir.toFile()));

				List<JavaFileObject> sources = Collections.singletonList(
					new InMemorySource(expectedClassName, scaffold.getSource()));
				List<String> options = Arrays.asList("-classpath", System.getProperty("java.class.path"),
					"-proc:none", // skip annotation processing — Lombok runs on the maker source, not on emitted code
					"-Xlint:none");

				boolean ok = compiler.getTask(null, fileManager, diagnostics, options, null, sources).call();
				if (!ok)
				{
					StringBuilder msg = new StringBuilder("Generated scaffold did not compile:\n");
					for (Diagnostic<? extends JavaFileObject> d : diagnostics.getDiagnostics())
					{
						if (d.getKind() == Diagnostic.Kind.ERROR)
						{
							msg.append("  line ").append(d.getLineNumber()).append(": ").append(d.getMessage(null)).append('\n');
						}
					}
					msg.append("\n--- generated source ---\n").append(scaffold.getSource());
					fail(msg.toString());
				}
			}
		}
		finally
		{
			deleteRecursive(outDir);
		}
	}

	private static void deleteRecursive(Path path) throws IOException
	{
		if (!Files.exists(path))
		{
			return;
		}
		try (var stream = Files.walk(path))
		{
			stream.sorted((a, b) -> b.getNameCount() - a.getNameCount()).forEach(p -> {
				try
				{
					Files.deleteIfExists(p);
				}
				catch (IOException ignored)
				{
				}
			});
		}
	}

	private static final class InMemorySource extends SimpleJavaFileObject
	{
		private final String content;

		InMemorySource(String simpleName, String content)
		{
			super(URI.create("string:///" + simpleName + Kind.SOURCE.extension), Kind.SOURCE);
			this.content = content;
		}

		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors)
		{
			return content;
		}
	}
}

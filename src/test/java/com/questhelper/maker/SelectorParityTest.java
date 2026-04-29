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

import com.questhelper.maker.HelperConstructModels.DraftOrderLine;
import com.questhelper.maker.HelperConstructModels.DraftOrderStepRequirement;
import com.questhelper.requirements.Requirement;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import net.runelite.api.Client;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Parity test between the two selector implementations:
 * {@link OrderStepRequirementSupport#buildRuntimeSelector} (drives the in-game preview)
 * and {@link OrderStepRequirementSupport#emitSelectorJava} (drives generated code).
 *
 * <p>Both walk the same tree shape. Any time one is changed and the other isn't, the four
 * representations of a helper start drifting silently. This test compiles the emitted Java
 * expression in-process and runs it against the same mocked {@link Client} as the runtime
 * selector — for every tree shape we care about, both must produce the same boolean.</p>
 */
class SelectorParityTest
{
	private static final int VARBIT_A = 4242;
	private static final int VARBIT_B = 7777;

	@Test
	void inlineVarbitLeafAgrees() throws Exception
	{
		DraftOrderStepRequirement tree = DraftOrderStepRequirement.varbit(VARBIT_A, 2, "EQUAL", "gate");
		assertParity(tree, varbits(VARBIT_A, 0));
		assertParity(tree, varbits(VARBIT_A, 1));
		assertParity(tree, varbits(VARBIT_A, 2));
		assertParity(tree, varbits(VARBIT_A, 3));
	}

	@Test
	void invertedVarbitAgrees() throws Exception
	{
		DraftOrderStepRequirement leaf = DraftOrderStepRequirement.varbit(VARBIT_A, 2, "EQUAL", "gate");
		DraftOrderStepRequirement tree = DraftOrderStepRequirement.invert(leaf);
		assertParity(tree, varbits(VARBIT_A, 1));
		assertParity(tree, varbits(VARBIT_A, 2));
	}

	@Test
	void andGroupAgrees() throws Exception
	{
		DraftOrderStepRequirement tree = DraftOrderStepRequirement.group("AND",
			DraftOrderStepRequirement.varbit(VARBIT_A, 1, "EQUAL", "a"),
			DraftOrderStepRequirement.varbit(VARBIT_B, 1, "EQUAL", "b"));
		assertParity(tree, varbits(VARBIT_A, 0, VARBIT_B, 0));
		assertParity(tree, varbits(VARBIT_A, 1, VARBIT_B, 0));
		assertParity(tree, varbits(VARBIT_A, 0, VARBIT_B, 1));
		assertParity(tree, varbits(VARBIT_A, 1, VARBIT_B, 1));
	}

	private static void assertParity(DraftOrderStepRequirement tree, Map<Integer, Integer> varbits) throws Exception
	{
		Client client = mock(Client.class);
		varbits.forEach((id, value) -> when(client.getVarbitValue(id)).thenReturn(value));

		DraftOrderLine line = new DraftOrderLine();
		Requirement runtime = OrderStepRequirementSupport.buildRuntimeSelector(tree, line, null, new HashMap<>(), null);
		assertNotNull(runtime, "buildRuntimeSelector returned null for " + describe(tree));

		List<String> warnings = new ArrayList<>();
		String emittedExpr = OrderStepRequirementSupport.emitSelectorJava(
			tree, line, null, new HashMap<>(), new HashMap<>(), new HashMap<>(), warnings);
		Requirement emitted = compileSelectorExpression(emittedExpr);

		boolean runtimeResult = runtime.check(client);
		boolean emittedResult = emitted.check(client);
		assertEquals(runtimeResult, emittedResult,
			"selector parity mismatch for " + describe(tree) + " with " + varbits + " — runtime emitted "
				+ runtimeResult + " but generated emitted " + emittedResult + " from `" + emittedExpr + "`");
	}

	private static Map<Integer, Integer> varbits(Object... pairs)
	{
		Map<Integer, Integer> out = new HashMap<>();
		for (int i = 0; i + 1 < pairs.length; i += 2)
		{
			out.put((Integer) pairs[i], (Integer) pairs[i + 1]);
		}
		return out;
	}

	private static String describe(DraftOrderStepRequirement tree)
	{
		if (tree == null) return "null";
		StringBuilder sb = new StringBuilder(tree.getKind());
		if (!tree.getChildren().isEmpty())
		{
			sb.append('[');
			for (int i = 0; i < tree.getChildren().size(); i++)
			{
				if (i > 0) sb.append(',');
				sb.append(describe(tree.getChildren().get(i)));
			}
			sb.append(']');
		}
		return sb.toString();
	}

	/**
	 * Wraps an emitted selector expression in a compilable harness class with a single
	 * {@code Requirement build()} method, compiles it, loads the class, and returns the
	 * Requirement instance. Lets us run the generated expression under the same mock client
	 * the runtime path sees.
	 */
	private static Requirement compileSelectorExpression(String expression) throws Exception
	{
		String harnessSource = ""
			+ "package com.questhelper.maker.harness;\n"
			+ "import com.questhelper.requirements.Requirement;\n"
			+ "import com.questhelper.requirements.conditional.Conditions;\n"
			+ "import com.questhelper.requirements.var.VarbitRequirement;\n"
			+ "import com.questhelper.requirements.zone.Zone;\n"
			+ "import com.questhelper.requirements.zone.ZoneRequirement;\n"
			+ "import com.questhelper.requirements.player.SkillRequirement;\n"
			+ "import com.questhelper.requirements.util.LogicType;\n"
			+ "import com.questhelper.requirements.util.Operation;\n"
			+ "import static com.questhelper.requirements.util.LogicHelper.and;\n"
			+ "import static com.questhelper.requirements.util.LogicHelper.nor;\n"
			+ "import static com.questhelper.requirements.util.LogicHelper.not;\n"
			+ "import static com.questhelper.requirements.util.LogicHelper.or;\n"
			+ "import net.runelite.api.Skill;\n"
			+ "import net.runelite.api.coords.WorldPoint;\n"
			+ "public class P {\n"
			+ "    public static Requirement build() { return " + expression + "; }\n"
			+ "}\n";

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		assertNotNull(compiler, "JDK with javac required to run this test (ToolProvider returned null)");

		Path outDir = Files.createTempDirectory("selector-parity-");
		try (StandardJavaFileManager fm = compiler.getStandardFileManager(null, null, null))
		{
			fm.setLocation(StandardLocation.CLASS_OUTPUT, Collections.singletonList(outDir.toFile()));
			List<JavaFileObject> units = Collections.singletonList(new InMemorySource("P", harnessSource));
			List<String> options = Arrays.asList(
				"-classpath", System.getProperty("java.class.path"),
				"-proc:none",
				"-Xlint:none");
			boolean ok = compiler.getTask(null, fm, null, options, null, units).call();
			if (!ok)
			{
				throw new IllegalStateException("Failed to compile harness for expression: " + expression
					+ "\n--- harness source ---\n" + harnessSource);
			}
			try (URLClassLoader loader = new URLClassLoader(new URL[]{outDir.toUri().toURL()}, SelectorParityTest.class.getClassLoader()))
			{
				Class<?> cls = Class.forName("com.questhelper.maker.harness.P", true, loader);
				Method m = cls.getMethod("build");
				return (Requirement) m.invoke(null);
			}
		}
		finally
		{
			deleteRecursive(outDir);
		}
	}

	private static void deleteRecursive(Path path) throws IOException
	{
		if (!Files.exists(path)) return;
		try (var stream = Files.walk(path))
		{
			stream.sorted((a, b) -> b.getNameCount() - a.getNameCount()).forEach(p -> {
				try { Files.deleteIfExists(p); } catch (IOException ignored) {}
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

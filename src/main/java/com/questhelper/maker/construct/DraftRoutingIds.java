package com.questhelper.maker.construct;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Pure helpers for merging primary + alternate ids used by maker UI, scaffold, and Tasks Tracker export.
 */
public final class DraftRoutingIds
{
	private DraftRoutingIds()
	{
	}

	public static List<Integer> parseCsvIntsStrict(String rawIdText)
	{
		List<Integer> out = new ArrayList<>();
		if (rawIdText == null || rawIdText.trim().isEmpty())
		{
			return out;
		}
		for (String part : rawIdText.split(","))
		{
			String p = part.trim();
			if (p.isEmpty())
			{
				continue;
			}
			int dash = p.indexOf('-');
			if (dash > 0 && dash < p.length() - 1)
			{
				String left = p.substring(0, dash).trim();
				String right = p.substring(dash + 1).trim();
				try
				{
					int start = Integer.parseInt(left);
					int end = Integer.parseInt(right);
					if (start <= end)
					{
						for (int id = start; id <= end; id++)
						{
							out.add(id);
						}
					}
					else
					{
						for (int id = start; id >= end; id--)
						{
							out.add(id);
						}
					}
					continue;
				}
				catch (NumberFormatException e)
				{
					return null;
				}
			}
			try
			{
				out.add(Integer.parseInt(p));
			}
			catch (NumberFormatException e)
			{
				return null;
			}
		}
		return out;
	}

	public static List<Integer> dedupeIntsPreserveOrder(List<Integer> in)
	{
		List<Integer> out = new ArrayList<>();
		LinkedHashSet<Integer> seen = new LinkedHashSet<>();
		for (Integer v : in)
		{
			if (v == null)
			{
				continue;
			}
			if (seen.add(v))
			{
				out.add(v);
			}
		}
		return out;
	}

	public static List<Integer> mergedStepOrRequirementIds(int primary, List<Integer> alternates)
	{
		List<Integer> merged = new ArrayList<>();
		merged.add(primary);
		if (alternates != null)
		{
			merged.addAll(alternates);
		}
		return dedupeIntsPreserveOrder(merged);
	}

	public static String formatCsvIds(List<Integer> ids)
	{
		if (ids.isEmpty())
		{
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < ids.size(); i++)
		{
			if (i > 0)
			{
				sb.append(", ");
			}
			sb.append(ids.get(i));
		}
		return sb.toString();
	}
}

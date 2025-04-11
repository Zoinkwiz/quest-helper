package com.questhelper.questinfo;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class HelperConfig
{
	@Getter
	String name;
	@Getter
	String key;
	@Getter
	Enum[] enums;
	public HelperConfig(String name, String key, Enum[] enums)
	{
		this.name = name;
		this.key = key;
		this.enums = enums;
	}

	public String[] getValues()
	{
		List<String> s = new ArrayList<>();
		for (Enum value : enums)
		{
			s.add(value.name());
		}
		return s.toArray(s.toArray(new String[0]));
	}
}

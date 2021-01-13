package com.questhelper.requirements;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.Favour;

@AllArgsConstructor
@Getter
public class FavourRequirement extends Requirement
{
	private Favour kourendFavour;
	private double percentage;

	@Override
	public boolean check(Client client)
	{
		return client.getVarbit() >= percentage;
	}

	@Override
	public String getDisplayText()
	{
		return null;
	}
}

package com.questhelper.requirements;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.Favour;

@AllArgsConstructor
@Getter
public class FavourRequirement extends Requirement
{
	private Favour houseFavour;
	private int percentage;

	@Override
	public boolean check(Client client)
	{
		int realFavour = client.getVar(houseFavour.getVarbit());
		return (realFavour / 10) >= percentage;
	}

	@Override
	public String getDisplayText()
	{
		return percentage + "% " + houseFavour.getName() + " favour";
	}
}

package com.questhelper.requirements.var;

import net.runelite.api.Client;

import java.util.function.ToIntBiFunction;

public enum VarType {
    VARBIT(Client::getVarbitValue),
    VARP(Client::getVarpValue);

    private final ToIntBiFunction<Client, Integer> getter;

    VarType(ToIntBiFunction<Client, Integer> getter)
    {
        this.getter = getter;
    }

    public int getValue(Client client, int id)
    {
        return getter.applyAsInt(client, id);
    }
}
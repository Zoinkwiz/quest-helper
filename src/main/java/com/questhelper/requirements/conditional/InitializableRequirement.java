/*
 *
 *  * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  *
 *  * 1. Redistributions of source code must retain the above copyright notice, this
 *  *    list of conditions and the following disclaimer.
 *  * 2. Redistributions in binary form must reproduce the above copyright notice,
 *  *    this list of conditions and the following disclaimer in the documentation
 *  *    and/or other materials provided with the distribution.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.questhelper.requirements.conditional;

import com.questhelper.requirements.Requirement;
import java.util.List;
import javax.annotation.Nonnull;
import net.runelite.api.Client;

/**
 * Represents a {@link Requirement} that has to be initialized before being
 * able to check it's requirement state.
 */
public interface InitializableRequirement extends Requirement
{
	/**
	 * Load the initial state of this requirement.
	 *
	 * @param client the {@link Client}
	 */
	void initialize(Client client);

	/**
	 * This is called when the client is loading in to a world or hopping between worlds.
	 * This is, primarily, used to update the internal state of a requirement while
	 * not resetting it entirely.
	 */
	void updateHandler();

	/**
	 * {@link InitializableRequirement}s can have their own requirements on top.
	 *
	 * @return a list of {@link Requirement} for this requirement. If there are no requirements, an empty list is returned.
	 */
	@Nonnull
	List<Requirement> getConditions();
}

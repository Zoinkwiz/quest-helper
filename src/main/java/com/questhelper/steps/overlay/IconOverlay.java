/*
 * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.steps.overlay;

import com.questhelper.Icon;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class IconOverlay
{
	public static BufferedImage createIconImage(BufferedImage newImage)
	{
		BufferedImage iconBackground = Icon.ICON_BACKGROUND.getImage();
		BufferedImage icon = new BufferedImage(iconBackground.getWidth(), iconBackground.getHeight(),
			BufferedImage.TYPE_INT_ARGB);
		Graphics tmpGraphics = icon.getGraphics();
		tmpGraphics.drawImage(iconBackground, 0, 0, null);

		float maxWidthOrHeight = Math.max(newImage.getHeight(), newImage.getWidth());
		int imageWidthScaled = (int) (iconBackground.getWidth() * newImage.getWidth() / maxWidthOrHeight);
		int imageHeightScaled = (int) (iconBackground.getHeight() * newImage.getHeight() / maxWidthOrHeight);

		int bufferWidth = iconBackground.getWidth() / 2 - imageWidthScaled / 2;
		bufferWidth = Math.max(bufferWidth, 0);
		int bufferHeight = iconBackground.getHeight() / 2 - imageHeightScaled / 2;
		bufferHeight = Math.max(bufferHeight, 0);

		tmpGraphics.drawImage(newImage, bufferWidth, bufferHeight, imageWidthScaled, imageHeightScaled, null);
		return icon;
	}
}

/*
 * Copyright (c) 2021, Zoinkwiz
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
package com.questhelper.quests.elementalworkshopii;

import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.QuestStep;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import net.runelite.api.widgets.Widget;

public class ConnectPipes extends QuestStep
{

	public ConnectPipes(QuestHelper questHelper)
	{
		super(questHelper, "Connect the pipes as marked..");
	}


	@Override
	public void makeWidgetOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		super.makeWidgetOverlayHint(graphics, plugin);

		Widget widget1 = client.getWidget(262, 41);
		Widget widget2 = client.getWidget(262, 42);
		Widget widget3 = client.getWidget(262, 37);
		Widget widget4 = client.getWidget(262, 38);
		Widget widget5 = client.getWidget(262, 39);
		Widget widget6 = client.getWidget(262, 40);

		if (widget1 != null)
		{
			drawConnection(graphics, widget2, widget3);
			drawConnection(graphics, widget1, widget6);
			drawConnection(graphics, widget4, widget5);
		}
	}

	public void drawConnection(Graphics2D graphics, Widget widget1, Widget widget2)
	{
		Line2D.Double line = new Line2D.Double(
			widget1.getCanvasLocation().getX() + (widget1.getWidth() / 2.0f),
			widget1.getCanvasLocation().getY() + (widget1.getHeight() / 2.0f),
			widget2.getCanvasLocation().getX() + (widget2.getWidth() / 2.0f),
			widget2.getCanvasLocation().getY() + (widget2.getHeight() / 2.0f));

		graphics.setColor(new Color(0, 255, 255, 65));
		graphics.fill(widget1.getBounds());
		graphics.setColor(questHelper.getConfig().targetOverlayColor());
		graphics.draw(widget1.getBounds());

		graphics.setColor(new Color(0, 255, 255, 65));
		graphics.fill(widget2.getBounds());
		graphics.setColor(questHelper.getConfig().targetOverlayColor());
		graphics.draw(widget2.getBounds());

		graphics.setStroke(new BasicStroke(3));
		graphics.draw(line);
	}

}

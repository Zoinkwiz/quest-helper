package com.questhelper.panel;

import com.questhelper.managers.HelperConstructManager;

import javax.swing.*;
import java.awt.*;

/**
 * Resizable top-level window for the Quest Helper Maker (construct editor).
 */
public final class HelperConstructFrame extends JFrame
{
	private final HelperConstructEditorPanel editor;

	public HelperConstructFrame(HelperConstructManager helperConstructManager)
	{
		super("Quest Helper Maker");
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setMinimumSize(new Dimension(720, 480));
		editor = new HelperConstructEditorPanel(helperConstructManager);
		setContentPane(editor);
		setSize(960, 640);
		setLocationByPlatform(true);
	}

	public void openWindow()
	{
		setVisible(true);
		setExtendedState(Frame.NORMAL);
		toFront();
		requestFocus();
	}

	/**
	 * Plugin shutdown: stop timers and destroy the window.
	 */
	public void disposeForShutdown()
	{
		editor.shutDown();
		dispose();
	}
}

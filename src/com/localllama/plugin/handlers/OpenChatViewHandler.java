package com.localllama.plugin.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class OpenChatViewHandler extends AbstractHandler {

	private static final String VIEW_ID = "com.localllama.plugin.ui.ChatView";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			IWorkbenchPage page = window.getActivePage();
			if (page != null) {
				try {
					page.showView(VIEW_ID);
				} catch (PartInitException e) {
					throw new ExecutionException("Failed to open Chat View", e);
				}
			}
		}
		return null;
	}
}

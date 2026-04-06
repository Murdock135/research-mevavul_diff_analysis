class run_1 {
@Override
	public void run(TaskMonitor monitor) {
		boolean ok = false;
		try {
			unjarArchive(monitor);
			ok = true;
		}
		catch (Exception e) {
			Msg.showError(this, null, null, null, e);
			message = message + " failed.";
		}
		message =
			"\"" + projectLocator.toString() + "\" from \"" + jarFile.getAbsolutePath() + "\"";
		if (monitor.isCancelled()) {
			message += " was cancelled by user.";
			// put everything back the way it was...
			plugin.cleanupRestoredProject(projectLocator);
		}
		else {
			message += ((ok) ? " succeeded." : " failed.");
			final boolean success = ok;
			Runnable r = () -> restoreCompleted(success);
			try {
				SwingUtilities.invokeAndWait(r);
			}
			catch (InterruptedException e1) {
			}
			catch (InvocationTargetException e1) {
			}
		}

		Msg.info(this, "Restore Archive: " + message);
	}
}

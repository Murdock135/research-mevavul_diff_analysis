class checkAccess_1 {
@Override
	public void checkAccess(ThreadGroup g) {
		if (RobocodeProperties.isSecurityOff()) {
			return;
		}
		Thread c = Thread.currentThread();

		if (isSafeThread(c)) {
			return;
		}
		super.checkAccess(g);

		final ThreadGroup cg = c.getThreadGroup();

		if (cg == null) {
			// What the heck is going on here?  JDK 1.3 is sending me a dead thread.
			// This crashes the entire jvm if I don't return here.
			return;
		}

		// Bug fix #382 Unable to run robocode.bat -- Access Control Exception
		if ("SeedGenerator Thread".equals(c.getName()) && "SeedGenerator ThreadGroup".equals(cg.getName())) {
			return; // The SeedGenerator might create a thread, which needs to be silently ignored
		}
		
		IHostedThread robotProxy = threadManager.getLoadedOrLoadingRobotProxy(c);

		if (robotProxy == null) {
			throw new AccessControlException("Preventing " + c.getName() + " from access to " + g.getName());			
		}

		if (cg.activeCount() > 5) {
			String message = "Robots are only allowed to create up to 5 threads!";

			robotProxy.punishSecurityViolation(message);
			throw new AccessControlException(message);
		}
	}
}

class checkAccess {
@Override
	public void checkAccess(Thread t) {
		if (RobocodeProperties.isSecurityOff()) {
			return;
		}

		Thread c = Thread.currentThread();

		if (isSafeThread(c)) {
			return;
		}
		super.checkAccess(t);

		// Threads belonging to other thread groups is not allowed to access threads belonging to other thread groups
		// Bug fix [3021140] Possible for robot to kill other robot threads.
		// In the following the thread group of the current thread must be in the thread group hierarchy of the
		// attacker thread; otherwise an AccessControlException must be thrown.

		boolean found = false;
		
		ThreadGroup cg = c.getThreadGroup();
		ThreadGroup tg = t.getThreadGroup();

		while (tg != null) {
			if (tg == cg) {
				found = true;
				break;
			}
			try {
				tg = tg.getParent();
			} catch (AccessControlException e) {
				// We expect an AccessControlException due to missing RuntimePermission modifyThreadGroup
				break;
			}
		}
		if (!found) {
			String message = "Preventing " + c.getName() + " from access to " + t.getName();
			IHostedThread robotProxy = threadManager.getLoadedOrLoadingRobotProxy(c);

			if (robotProxy != null) {
				robotProxy.punishSecurityViolation(message);
			}
			throw new AccessControlException(message);
		}
	}
}

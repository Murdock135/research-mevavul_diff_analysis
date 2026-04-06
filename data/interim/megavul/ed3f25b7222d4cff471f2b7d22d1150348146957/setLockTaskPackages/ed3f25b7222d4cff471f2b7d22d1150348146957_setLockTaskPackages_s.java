class setLockTaskPackages {
@Override
    public void setLockTaskPackages(ComponentName who, String[] packages)
            throws SecurityException {
        Objects.requireNonNull(who, "ComponentName is null");
        Objects.requireNonNull(packages, "packages is null");
        final CallerIdentity caller = getCallerIdentity(who);

        synchronized (getLockObject()) {
            enforceCanCallLockTaskLocked(caller);
            checkCanExecuteOrThrowUnsafe(DevicePolicyManager.OPERATION_SET_LOCK_TASK_PACKAGES);
            final int userHandle = caller.getUserId();
            setLockTaskPackagesLocked(userHandle, new ArrayList<>(Arrays.asList(packages)));
        }
    }
}

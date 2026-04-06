class setTrustAgentConfiguration {
@Override
    public void setTrustAgentConfiguration(ComponentName admin, ComponentName agent,
            PersistableBundle args, boolean parent) {
        if (!mHasFeature || !mLockPatternUtils.hasSecureLockScreen()) {
            return;
        }
        Objects.requireNonNull(admin, "admin is null");
        Objects.requireNonNull(agent, "agent is null");
        enforceMaxPackageNameLength(agent.getPackageName());
        final String agentAsString = agent.flattenToString();
        enforceMaxStringLength(agentAsString, "agent name");
        if (args != null) {
            enforceMaxStringLength(args, "args");
        }
        final int userHandle = UserHandle.getCallingUserId();
        synchronized (getLockObject()) {
            ActiveAdmin ap = getActiveAdminForCallerLocked(admin,
                    DeviceAdminInfo.USES_POLICY_DISABLE_KEYGUARD_FEATURES, parent);
            checkCanExecuteOrThrowUnsafe(
                    DevicePolicyManager.OPERATION_SET_TRUST_AGENT_CONFIGURATION);

            ap.trustAgentInfos.put(agent.flattenToString(), new TrustAgentInfo(args));
            saveSettingsLocked(userHandle);
        }
    }
}

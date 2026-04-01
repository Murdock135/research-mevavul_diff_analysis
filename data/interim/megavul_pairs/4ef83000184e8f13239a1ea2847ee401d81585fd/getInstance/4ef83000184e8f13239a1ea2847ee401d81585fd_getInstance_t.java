class getInstance {
public static UnsafeAccess getInstance() {
        SecurityCheck.AccessLimiter accessLimiter = SecurityCheck.getInstance().getLimiter();
        if (accessLimiter != null) accessLimiter.preGetUnsafeAccess();
        return INSTANCE;
    }
}

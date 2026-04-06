class getInstance {
public static UnsafeAccess getInstance() {
        SecurityCheck.getInstance();
        return INSTANCE;
    }
}

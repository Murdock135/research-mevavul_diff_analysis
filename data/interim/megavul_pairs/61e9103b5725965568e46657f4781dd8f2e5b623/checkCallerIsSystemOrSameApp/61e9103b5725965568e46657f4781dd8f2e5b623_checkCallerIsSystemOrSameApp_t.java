class checkCallerIsSystemOrSameApp {
private static void checkCallerIsSystemOrSameApp(String pkg) {
        if (isCallerSystem()) {
            return;
        }
        checkCallerIsSameApp(pkg);
    }
}

class verifyShortcutInfoPackage {
private void verifyShortcutInfoPackage(String callerPackage, ShortcutInfo si) {
        if (si == null) {
            return;
        }
        if (!Objects.equals(callerPackage, si.getPackage())) {
            android.util.EventLog.writeEvent(0x534e4554, "109824443", -1, "");
            throw new SecurityException("Shortcut package name mismatch");
        }
    }
}

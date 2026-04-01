class getAttributeResolutionStack {
int[] getAttributeResolutionStack(long themePtr, @AttrRes int defStyleAttr,
            @StyleRes int defStyleRes, @StyleRes int xmlStyle) {
        synchronized (this) {
            ensureValidLocked();
            return nativeAttributeResolutionStack(
                    mObject, themePtr, xmlStyle, defStyleAttr, defStyleRes);
        }
    }
}

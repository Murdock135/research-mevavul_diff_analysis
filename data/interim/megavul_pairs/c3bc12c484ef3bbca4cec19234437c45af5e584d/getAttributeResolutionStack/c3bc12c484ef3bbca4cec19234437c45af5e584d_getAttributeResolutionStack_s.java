class getAttributeResolutionStack {
int[] getAttributeResolutionStack(long themePtr, @AttrRes int defStyleAttr,
            @StyleRes int defStyleRes, @StyleRes int xmlStyle) {
        synchronized (this) {
            return nativeAttributeResolutionStack(
                    mObject, themePtr, xmlStyle, defStyleAttr, defStyleRes);
        }
    }
}

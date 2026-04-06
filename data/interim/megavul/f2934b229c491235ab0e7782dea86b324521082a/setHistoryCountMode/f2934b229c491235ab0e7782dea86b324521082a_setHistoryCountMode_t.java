class setHistoryCountMode {
public void setHistoryCountMode(@Nonnull HistoryCountModeEnum theHistoryCountMode) {

		Validate.notNull(theHistoryCountMode, "theHistoryCountMode must not be null");
		myHistoryCountMode = theHistoryCountMode;
	}
}

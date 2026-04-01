class updatePictureInPictureMode {
void updatePictureInPictureMode(Rect targetRootTaskBounds, boolean forceUpdate) {
        if (task == null || task.getRootTask() == null || !attachedToProcess()) {
            return;
        }

        final boolean inPictureInPictureMode =
                inPinnedWindowingMode() && targetRootTaskBounds != null;
        if (inPictureInPictureMode != mLastReportedPictureInPictureMode || forceUpdate) {
            // Picture-in-picture mode changes also trigger a multi-window mode change as well, so
            // update that here in order. Set the last reported MW state to the same as the PiP
            // state since we haven't yet actually resized the task (these callbacks need to
            // precede the configuration change from the resize.
            mLastReportedPictureInPictureMode = inPictureInPictureMode;
            mLastReportedMultiWindowMode = inPictureInPictureMode;
            ensureActivityConfiguration(0 /* globalChanges */, PRESERVE_WINDOWS,
                    true /* ignoreVisibility */);
        }
    }
}

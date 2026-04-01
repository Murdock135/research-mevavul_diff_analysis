class dispatchGesture {
public final boolean dispatchGesture(@NonNull GestureDescription gesture,
            @Nullable GestureResultCallback callback,
            @Nullable Handler handler) {
        final IAccessibilityServiceConnection connection =
                AccessibilityInteractionClient.getInstance().getConnection(
                        mConnectionId);
        if (connection == null) {
            return false;
        }
        List<MotionEvent> events = MotionEventGenerator.getMotionEventsFromGestureDescription(
                gesture, 100);
        try {
            synchronized (mLock) {
                mGestureStatusCallbackSequence++;
                if (callback != null) {
                    if (mGestureStatusCallbackInfos == null) {
                        mGestureStatusCallbackInfos = new SparseArray<>();
                    }
                    GestureResultCallbackInfo callbackInfo = new GestureResultCallbackInfo(gesture,
                            callback, handler);
                    mGestureStatusCallbackInfos.put(mGestureStatusCallbackSequence, callbackInfo);
                }
                connection.sendMotionEvents(mGestureStatusCallbackSequence,
                        new ParceledListSlice<>(events));
            }
        } catch (RemoteException re) {
            throw new RuntimeException(re);
        }
        return true;
    }
}

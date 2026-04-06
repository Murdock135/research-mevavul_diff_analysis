class kill {
void kill(String reason, boolean noisy) {
        if (!killedByAm) {
            Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "kill");
            if (noisy) {
                Slog.i(TAG, "Killing " + toShortString() + " (adj " + setAdj + "): " + reason);
            }
            EventLog.writeEvent(EventLogTags.AM_KILL, userId, pid, processName, setAdj, reason);
            Process.killProcessQuiet(pid);
            Process.killProcessGroup(info.uid, pid);
            if (!persistent) {
                killed = true;
                killedByAm = true;
            }
            Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
        }
    }
}

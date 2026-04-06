class addActivity {
public final void addActivity(PackageParser.Activity a, String type) {
            final boolean systemApp = a.info.applicationInfo.isSystemApp();
            mActivities.put(a.getComponentName(), a);
            if (DEBUG_SHOW_INFO)
                Log.v(
                TAG, "  " + type + " " +
                (a.info.nonLocalizedLabel != null ? a.info.nonLocalizedLabel : a.info.name) + ":");
            if (DEBUG_SHOW_INFO)
                Log.v(TAG, "    Class=" + a.info.name);
            final int NI = a.intents.size();
            for (int j=0; j<NI; j++) {
                PackageParser.ActivityIntentInfo intent = a.intents.get(j);
                if ("activity".equals(type)) {
                    final PackageSetting ps =
                            mSettings.getDisabledSystemPkgLPr(intent.activity.info.packageName);
                    final List<PackageParser.Activity> systemActivities =
                            ps != null && ps.pkg != null ? ps.pkg.activities : null;
                    adjustPriority(systemActivities, intent);
                }
                if (DEBUG_SHOW_INFO) {
                    Log.v(TAG, "    IntentFilter:");
                    intent.dump(new LogPrinter(Log.VERBOSE, TAG), "      ");
                }
                if (!intent.debugCheck()) {
                    Log.w(TAG, "==> For Activity " + a.info.name);
                }
                addFilter(intent);
            }
        }
}

class getCurrentSyncsAsUser {
public List<SyncInfo> getCurrentSyncsAsUser(int userId) {
        enforceCrossUserPermission(userId,
                "no permission to read the sync settings for user " + userId);
        mContext.enforceCallingOrSelfPermission(Manifest.permission.READ_SYNC_STATS,
                "no permission to read the sync stats");

        long identityToken = clearCallingIdentity();
        try {
            return getSyncManager().getSyncStorageEngine().getCurrentSyncsCopy(userId);
        } finally {
            restoreCallingIdentity(identityToken);
        }
    }
}

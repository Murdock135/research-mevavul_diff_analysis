class executeSql {
public int executeSql(String sql, Object[] bindArgs) throws SQLException {
        acquireReference();
        try {
            final int statementType = DatabaseUtils.getSqlStatementType(sql);
            if (statementType == DatabaseUtils.STATEMENT_ATTACH) {
                boolean disableWal = false;
                synchronized (mLock) {
                    if (!mHasAttachedDbsLocked) {
                        mHasAttachedDbsLocked = true;
                        disableWal = true;
                        mConnectionPoolLocked.disableIdleConnectionHandler();
                    }
                }
                if (disableWal) {
                    disableWriteAheadLogging();
                }
            }

            try (SQLiteStatement statement = new SQLiteStatement(this, sql, bindArgs)) {
                return statement.executeUpdateDelete();
            } finally {
                // If schema was updated, close non-primary connections, otherwise they might
                // have outdated schema information
                if (statementType == DatabaseUtils.STATEMENT_DDL) {
                    mConnectionPoolLocked.closeAvailableNonPrimaryConnectionsAndLogExceptions();
                }
            }
        } finally {
            releaseReference();
        }
    }
}

class appendWhereEscapeString {
public void appendWhereEscapeString(String inWhere) {
        if (mWhereClause == null) {
            mWhereClause = new StringBuilder(inWhere.length() + 16);
        }
        DatabaseUtils.appendEscapedSQLString(mWhereClause, inWhere);
    }
}

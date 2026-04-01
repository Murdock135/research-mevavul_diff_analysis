class appendWhereEscapeString {
public void appendWhereEscapeString(String inWhere) {
        if (mWhereClause == null) {
            mWhereClause = new StringBuilder(inWhere.length() + 16);
        }
        if (mWhereClause.length() == 0) {
            mWhereClause.append('(');
        }
        DatabaseUtils.appendEscapedSQLString(mWhereClause, inWhere);
    }
}

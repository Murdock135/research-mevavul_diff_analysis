class appendWhere {
public void appendWhere(CharSequence inWhere) {
        if (mWhereClause == null) {
            mWhereClause = new StringBuilder(inWhere.length() + 16);
        }
        mWhereClause.append(inWhere);
    }
}

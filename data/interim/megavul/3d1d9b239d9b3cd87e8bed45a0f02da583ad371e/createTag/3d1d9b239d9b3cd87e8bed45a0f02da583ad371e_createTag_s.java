class createTag {
public Tag createTag(ShaarliAccount masterAccount, String value) {
        Tag tag = new Tag();
        tag.setMasterAccount(masterAccount);
        tag.setValue(value.trim());

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.TAGS_COLUMN_ID_ACCOUNT, masterAccount.getId());
        values.put(MySQLiteHelper.TAGS_COLUMN_TAG, tag.getValue());

        // If existing, do nothing :
        Cursor cursor = db.query(MySQLiteHelper.TABLE_TAGS, allColumns,
                MySQLiteHelper.TAGS_COLUMN_ID_ACCOUNT + " = " + tag.getMasterAccountId() + " AND " +
                        MySQLiteHelper.TAGS_COLUMN_TAG + " = '" + tag.getValue() + "'",
                null, null, null, null);
        try {
            cursor.moveToFirst();
            if (cursor.isAfterLast()) {
                long insertId = db.insert(MySQLiteHelper.TABLE_TAGS, null, values);
                tag.setId(insertId);
                return tag;
            } else {
                tag = cursorToTag(cursor);
            }
        } catch (Exception e){
            tag = null;
        } finally {
            cursor.close();
        }
        return tag;
    }
}

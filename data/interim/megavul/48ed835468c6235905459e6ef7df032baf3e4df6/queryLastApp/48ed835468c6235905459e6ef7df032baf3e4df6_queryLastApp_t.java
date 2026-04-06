class queryLastApp {
protected queryData queryLastApp(SQLiteDatabase db,
                String app_id, String content_type) {
            if (LOCAL_LOGV) Log.v(LOG_TAG, "queryLastApp app_id: " + app_id
                    + " content_type: " +  content_type);

            Cursor cur = db.query(APPID_TABLE_NAME,
                    new String[] {"install_order", "package_name", "class_name",
                    "app_type", "need_signature", "further_processing"},
                    "x_wap_application=? and content_type=?",
                    new String[] {app_id, content_type},
                    null /* groupBy */,
                    null /* having */,
                    "install_order desc" /* orderBy */);

            queryData ret = null;

            if (cur.moveToNext()) {
                ret = new queryData();
                ret.installOrder = cur.getInt(cur.getColumnIndex("install_order"));
                ret.packageName = cur.getString(cur.getColumnIndex("package_name"));
                ret.className = cur.getString(cur.getColumnIndex("class_name"));
                ret.appType = cur.getInt(cur.getColumnIndex("app_type"));
                ret.needSignature = cur.getInt(cur.getColumnIndex("need_signature"));
                ret.furtherProcessing = cur.getInt(cur.getColumnIndex("further_processing"));
            }
            cur.close();
            return ret;
        }
}

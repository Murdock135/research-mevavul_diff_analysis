class queryLastApp {
protected queryData queryLastApp(SQLiteDatabase db,
                String app_id, String content_type) {
            String sql = "select install_order, package_name, class_name, "
                    + " app_type, need_signature, further_processing"
                    + " from " + APPID_TABLE_NAME
                    + " where x_wap_application=\'" + app_id + "\'"
                    + " and content_type=\'" + content_type + "\'"
                    + " order by install_order desc";
            if (DEBUG_SQL) Log.v(LOG_TAG, "sql: " + sql);
            Cursor cur = db.rawQuery(sql, null);
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

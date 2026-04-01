class getLinearIntValues {
@Override
    public IntValues getLinearIntValues(String tableName, DownSampling downsampling, List<String> ids,
                                        String valueCName) throws IOException {
        StringBuilder sql = new StringBuilder("select id, " + valueCName + " from " + tableName + " where id in (");
        List<Object> parameters = new ArrayList();
        for (int i = 0; i < ids.size(); i++) {
            if (i == 0) {
                sql.append("?");
            } else {
                sql.append(",?");
            }
            parameters.add(ids.get(i));
        }
        sql.append(")");

        IntValues intValues = new IntValues();

        try (Connection connection = h2Client.getConnection()) {

            try (ResultSet resultSet = h2Client.executeQuery(
                connection, sql.toString(), parameters.toArray(new Object[0]))) {
                while (resultSet.next()) {
                    KVInt kv = new KVInt();
                    kv.setId(resultSet.getString("id"));
                    kv.setValue(resultSet.getLong(valueCName));
                    intValues.addKVInt(kv);
                }
            }
        } catch (SQLException e) {
            throw new IOException(e);
        }

        return orderWithDefault0(intValues, ids);
    }
}

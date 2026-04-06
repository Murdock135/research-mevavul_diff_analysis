class getLinearIntValues {
@Override
    public IntValues getLinearIntValues(String tableName, DownSampling downsampling, List<String> ids,
                                        String valueCName) throws IOException {
        StringBuilder idValues = new StringBuilder();
        for (int valueIdx = 0; valueIdx < ids.size(); valueIdx++) {
            if (valueIdx != 0) {
                idValues.append(",");
            }
            idValues.append("'").append(ids.get(valueIdx)).append("'");
        }

        IntValues intValues = new IntValues();

        try (Connection connection = h2Client.getConnection()) {
            try (ResultSet resultSet = h2Client.executeQuery(
                connection, "select id, " + valueCName + " from " + tableName + " where id in (" + idValues
                    .toString() + ")")) {
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

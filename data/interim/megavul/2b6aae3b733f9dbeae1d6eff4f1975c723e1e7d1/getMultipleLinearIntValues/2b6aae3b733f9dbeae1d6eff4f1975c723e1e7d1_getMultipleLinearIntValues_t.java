class getMultipleLinearIntValues {
@Override
    public IntValues[] getMultipleLinearIntValues(String tableName,
                                                  DownSampling downsampling,
                                                  List<String> ids,
                                                  final List<Integer> linearIndex,
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

        IntValues[] intValuesArray = new IntValues[linearIndex.size()];
        for (int i = 0; i < intValuesArray.length; i++) {
            intValuesArray[i] = new IntValues();
        }

        try (Connection connection = h2Client.getConnection()) {
            try (ResultSet resultSet = h2Client.executeQuery(
                connection, sql.toString(), parameters.toArray(new Object[0]))) {
                while (resultSet.next()) {
                    String id = resultSet.getString("id");

                    IntKeyLongValueHashMap multipleValues = new IntKeyLongValueHashMap(5);
                    multipleValues.toObject(resultSet.getString(valueCName));

                    for (int i = 0; i < linearIndex.size(); i++) {
                        Integer index = linearIndex.get(i);
                        KVInt kv = new KVInt();
                        kv.setId(id);
                        kv.setValue(multipleValues.get(index).getValue());
                        intValuesArray[i].addKVInt(kv);
                    }
                }
            }
        } catch (SQLException e) {
            throw new IOException(e);
        }

        return orderWithDefault0(intValuesArray, ids);
    }
}

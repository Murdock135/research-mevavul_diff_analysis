class getMultipleLinearIntValues {
@Override
    public IntValues[] getMultipleLinearIntValues(String tableName,
                                                  DownSampling downsampling,
                                                  List<String> ids,
                                                  final List<Integer> linearIndex,
                                                  String valueCName) throws IOException {
        StringBuilder idValues = new StringBuilder();
        for (int valueIdx = 0; valueIdx < ids.size(); valueIdx++) {
            if (valueIdx != 0) {
                idValues.append(",");
            }
            idValues.append("'").append(ids.get(valueIdx)).append("'");
        }

        IntValues[] intValuesArray = new IntValues[linearIndex.size()];
        for (int i = 0; i < intValuesArray.length; i++) {
            intValuesArray[i] = new IntValues();
        }

        try (Connection connection = h2Client.getConnection()) {
            try (ResultSet resultSet = h2Client.executeQuery(
                connection, "select id, " + valueCName + " from " + tableName + " where id in (" + idValues
                    .toString() + ")")) {
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

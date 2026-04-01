class getThermodynamic {
@Override
    public Thermodynamic getThermodynamic(String tableName, DownSampling downsampling, List<String> ids,
                                          String valueCName) throws IOException {
        StringBuilder sql = new StringBuilder(
            "select " + ThermodynamicMetrics.STEP + " step, " + ThermodynamicMetrics.NUM_OF_STEPS + " num_of_steps, " + ThermodynamicMetrics.DETAIL_GROUP + " detail_group, " + "id " + " from " + tableName + " where id in (");
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

        List<List<Long>> thermodynamicValueCollection = new ArrayList<>();
        Map<String, List<Long>> thermodynamicValueMatrix = new HashMap<>();

        try (Connection connection = h2Client.getConnection()) {
            Thermodynamic thermodynamic = new Thermodynamic();
            int numOfSteps = 0;
            int axisYStep = 0;
            try (ResultSet resultSet = h2Client.executeQuery(
                connection, sql.toString(), parameters.toArray(new Object[0]))) {

                while (resultSet.next()) {
                    axisYStep = resultSet.getInt("step");
                    String id = resultSet.getString("id");
                    numOfSteps = resultSet.getInt("num_of_steps") + 1;
                    String value = resultSet.getString("detail_group");
                    IntKeyLongValueHashMap intKeyLongValues = new IntKeyLongValueHashMap(5);
                    intKeyLongValues.toObject(value);

                    List<Long> axisYValues = new ArrayList<>();
                    for (int i = 0; i < numOfSteps; i++) {
                        axisYValues.add(0L);
                    }

                    for (IntKeyLongValue intKeyLongValue : intKeyLongValues.values()) {
                        axisYValues.set(intKeyLongValue.getKey(), intKeyLongValue.getValue());
                    }

                    thermodynamicValueMatrix.put(id, axisYValues);
                }

                // try to add default values when there is no data in that time bucket.
                ids.forEach(id -> {
                    if (thermodynamicValueMatrix.containsKey(id)) {
                        thermodynamicValueCollection.add(thermodynamicValueMatrix.get(id));
                    } else {
                        thermodynamicValueCollection.add(new ArrayList<>());
                    }
                });
            }

            thermodynamic.fromMatrixData(thermodynamicValueCollection, numOfSteps);
            thermodynamic.setAxisYStep(axisYStep);

            return thermodynamic;
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }
}

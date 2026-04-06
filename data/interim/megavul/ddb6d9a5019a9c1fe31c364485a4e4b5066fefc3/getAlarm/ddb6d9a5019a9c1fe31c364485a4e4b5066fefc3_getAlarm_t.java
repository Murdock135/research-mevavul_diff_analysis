class getAlarm {
@Override
    public Alarms getAlarm(Integer scopeId, String keyword, int limit, int from, long startTB,
        long endTB) throws IOException {
        StringBuilder sql = new StringBuilder();
        List<Object> parameters = new ArrayList<>(10);
        sql.append("from ").append(AlarmRecord.INDEX_NAME).append(" where ");
        sql.append(" 1=1 ");
        if (Objects.nonNull(scopeId)) {
            sql.append(" and ").append(AlarmRecord.SCOPE).append(" = ?");
            parameters.add(scopeId.intValue());
        }
        if (startTB != 0 && endTB != 0) {
            sql.append(" and ").append(AlarmRecord.TIME_BUCKET).append(" >= ?");
            parameters.add(startTB);
            sql.append(" and ").append(AlarmRecord.TIME_BUCKET).append(" <= ?");
            parameters.add(endTB);
        }

        if (!Strings.isNullOrEmpty(keyword)) {
            sql.append(" and ").append(AlarmRecord.ALARM_MESSAGE).append(" like concat('%',?,'%') ");
            parameters.add(keyword);
        }
        sql.append(" order by ").append(AlarmRecord.START_TIME).append(" desc ");

        Alarms alarms = new Alarms();
        try (Connection connection = client.getConnection()) {

            try (ResultSet resultSet = client.executeQuery(connection, "select count(1) total " + sql.toString(), parameters
                .toArray(new Object[0]))) {
                while (resultSet.next()) {
                    alarms.setTotal(resultSet.getInt("total"));
                }
            }

            this.buildLimit(sql, from, limit);

            try (ResultSet resultSet = client.executeQuery(connection, "select * " + sql.toString(), parameters.toArray(new Object[0]))) {
                while (resultSet.next()) {
                    AlarmMessage message = new AlarmMessage();
                    message.setId(resultSet.getString(AlarmRecord.ID0));
                    message.setMessage(resultSet.getString(AlarmRecord.ALARM_MESSAGE));
                    message.setStartTime(resultSet.getLong(AlarmRecord.START_TIME));
                    message.setScope(Scope.Finder.valueOf(resultSet.getInt(AlarmRecord.SCOPE)));
                    message.setScopeId(resultSet.getInt(AlarmRecord.SCOPE));

                    alarms.getMsgs().add(message);
                }
            }
        } catch (SQLException e) {
            throw new IOException(e);
        }

        return alarms;
    }
}

class queryBasicTraces {
@Override
    public TraceBrief queryBasicTraces(long startSecondTB,
                                       long endSecondTB,
                                       long minDuration,
                                       long maxDuration,
                                       String endpointName,
                                       String serviceId,
                                       String serviceInstanceId,
                                       String endpointId,
                                       String traceId,
                                       int limit,
                                       int from,
                                       TraceState traceState,
                                       QueryOrder queryOrder) throws IOException {
        StringBuilder sql = new StringBuilder();
        List<Object> parameters = new ArrayList<>(10);

        sql.append("from ").append(SegmentRecord.INDEX_NAME).append(" where ");
        sql.append(" 1=1 ");
        if (startSecondTB != 0 && endSecondTB != 0) {
            sql.append(" and ").append(SegmentRecord.TIME_BUCKET).append(" >= ?");
            parameters.add(startSecondTB);
            sql.append(" and ").append(SegmentRecord.TIME_BUCKET).append(" <= ?");
            parameters.add(endSecondTB);
        }
        if (minDuration != 0 || maxDuration != 0) {
            if (minDuration != 0) {
                sql.append(" and ").append(SegmentRecord.LATENCY).append(" >= ?");
                parameters.add(minDuration);
            }
            if (maxDuration != 0) {
                sql.append(" and ").append(SegmentRecord.LATENCY).append(" <= ?");
                parameters.add(maxDuration);
            }
        }
        if (!Strings.isNullOrEmpty(endpointName)) {
            sql.append(" and ").append(SegmentRecord.ENDPOINT_NAME).append(" like '%" + endpointName + "%'");
        }
        if (StringUtil.isNotEmpty(serviceId)) {
            sql.append(" and ").append(SegmentRecord.SERVICE_ID).append(" = ?");
            parameters.add(serviceId);
        }
        if (StringUtil.isNotEmpty(serviceInstanceId)) {
            sql.append(" and ").append(SegmentRecord.SERVICE_INSTANCE_ID).append(" = ?");
            parameters.add(serviceInstanceId);
        }
        if (!Strings.isNullOrEmpty(endpointId)) {
            sql.append(" and ").append(SegmentRecord.ENDPOINT_ID).append(" = ?");
            parameters.add(endpointId);
        }
        if (!Strings.isNullOrEmpty(traceId)) {
            sql.append(" and ").append(SegmentRecord.TRACE_ID).append(" = ?");
            parameters.add(traceId);
        }
        switch (traceState) {
            case ERROR:
                sql.append(" and ").append(SegmentRecord.IS_ERROR).append(" = ").append(BooleanUtils.TRUE);
                break;
            case SUCCESS:
                sql.append(" and ").append(SegmentRecord.IS_ERROR).append(" = ").append(BooleanUtils.FALSE);
                break;
        }
        switch (queryOrder) {
            case BY_START_TIME:
                sql.append(" order by ").append(SegmentRecord.START_TIME).append(" ").append(SortOrder.DESC);
                break;
            case BY_DURATION:
                sql.append(" order by ").append(SegmentRecord.LATENCY).append(" ").append(SortOrder.DESC);
                break;
        }

        TraceBrief traceBrief = new TraceBrief();
        try (Connection connection = h2Client.getConnection()) {

            try (ResultSet resultSet = h2Client.executeQuery(connection, buildCountStatement(sql.toString()), parameters
                .toArray(new Object[0]))) {
                while (resultSet.next()) {
                    traceBrief.setTotal(resultSet.getInt("total"));
                }
            }

            buildLimit(sql, from, limit);

            try (ResultSet resultSet = h2Client.executeQuery(
                connection, "select * " + sql.toString(), parameters.toArray(new Object[0]))) {
                while (resultSet.next()) {
                    BasicTrace basicTrace = new BasicTrace();

                    basicTrace.setSegmentId(resultSet.getString(SegmentRecord.SEGMENT_ID));
                    basicTrace.setStart(resultSet.getString(SegmentRecord.START_TIME));
                    basicTrace.getEndpointNames().add(resultSet.getString(SegmentRecord.ENDPOINT_NAME));
                    basicTrace.setDuration(resultSet.getInt(SegmentRecord.LATENCY));
                    basicTrace.setError(BooleanUtils.valueToBoolean(resultSet.getInt(SegmentRecord.IS_ERROR)));
                    String traceIds = resultSet.getString(SegmentRecord.TRACE_ID);
                    basicTrace.getTraceIds().add(traceIds);
                    traceBrief.getTraces().add(basicTrace);
                }
            }
        } catch (SQLException e) {
            throw new IOException(e);
        }

        return traceBrief;
    }
}

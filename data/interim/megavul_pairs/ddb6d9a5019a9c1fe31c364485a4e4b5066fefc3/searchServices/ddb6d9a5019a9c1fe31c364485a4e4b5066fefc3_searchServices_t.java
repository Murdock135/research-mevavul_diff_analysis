class searchServices {
@Override
    public List<Service> searchServices(long startTimestamp, long endTimestamp, String keyword) throws IOException {
        StringBuilder sql = new StringBuilder();
        List<Object> condition = new ArrayList<>(5);
        sql.append("select * from ").append(ServiceTraffic.INDEX_NAME).append(" where ");
        sql.append(ServiceTraffic.NODE_TYPE).append("=?");
        condition.add(NodeType.Normal.value());
        if (!Strings.isNullOrEmpty(keyword)) {
            sql.append(" and ").append(ServiceTraffic.NAME).append(" like concat('%',?,'%')");
            condition.add(keyword);
        }
        sql.append(" limit ").append(metadataQueryMaxSize);

        try (Connection connection = h2Client.getConnection()) {
            try (ResultSet resultSet = h2Client.executeQuery(
                connection, sql.toString(), condition.toArray(new Object[0]))) {
                return buildServices(resultSet);
            }
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }
}

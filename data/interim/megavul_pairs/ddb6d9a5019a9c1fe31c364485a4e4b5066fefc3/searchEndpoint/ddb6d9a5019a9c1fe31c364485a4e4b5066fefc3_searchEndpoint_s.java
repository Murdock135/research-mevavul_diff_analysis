class searchEndpoint {
@Override
    public List<Endpoint> searchEndpoint(String keyword, String serviceId, int limit) throws IOException {
        StringBuilder sql = new StringBuilder();
        List<Object> condition = new ArrayList<>(5);
        sql.append("select * from ").append(EndpointTraffic.INDEX_NAME).append(" where ");
        sql.append(EndpointTraffic.SERVICE_ID).append("=?");
        condition.add(serviceId);
        if (!Strings.isNullOrEmpty(keyword)) {
            sql.append(" and ").append(EndpointTraffic.NAME).append(" like '%").append(keyword).append("%' ");
        }
        sql.append(" limit ").append(limit);

        List<Endpoint> endpoints = new ArrayList<>();
        try (Connection connection = h2Client.getConnection()) {
            try (ResultSet resultSet = h2Client.executeQuery(
                connection, sql.toString(), condition.toArray(new Object[0]))) {

                while (resultSet.next()) {
                    Endpoint endpoint = new Endpoint();
                    endpoint.setId(resultSet.getString(H2TableInstaller.ID_COLUMN));
                    endpoint.setName(resultSet.getString(EndpointTraffic.NAME));
                    endpoints.add(endpoint);
                }
            }
        } catch (SQLException e) {
            throw new IOException(e);
        }
        return endpoints;
    }
}

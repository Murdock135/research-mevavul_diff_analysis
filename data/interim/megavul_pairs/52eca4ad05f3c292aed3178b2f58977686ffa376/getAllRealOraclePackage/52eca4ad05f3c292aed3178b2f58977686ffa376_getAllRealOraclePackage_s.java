class getAllRealOraclePackage {
private List<OraclePackage> getAllRealOraclePackage(OBridgeConfiguration c) {
        String query = "select object_name from user_objects where object_type = 'PACKAGE' and object_name like '" + c.getPackagesLike() + "'";
        return jdbcTemplate.query(query, (resultSet, i) -> {
            OraclePackage p = new OraclePackage();
            p.setName(resultSet.getString("object_name"));
            p.setProcedureList(getAllProcedure(resultSet.getString("object_name"), ""));
            return p;
        });
    }
}

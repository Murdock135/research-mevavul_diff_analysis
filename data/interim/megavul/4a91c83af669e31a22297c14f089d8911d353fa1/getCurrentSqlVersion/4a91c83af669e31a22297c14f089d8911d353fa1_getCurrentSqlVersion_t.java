class getCurrentSqlVersion {
public static String getCurrentSqlVersion(String jdbcUrl, String userName, String password, String deriveClass) {
        Connection connect = null;
        try {
            connect = getConnection(jdbcUrl, userName, password, deriveClass);
            if (connect != null) {
                String queryVersionSQL = "select value from website where name = ?";
                try (PreparedStatement ps = connect.prepareStatement(queryVersionSQL)) {
                    ps.setString(1, Constants.ZRLOG_SQL_VERSION_KEY);
                    try (ResultSet resultSet = ps.executeQuery()) {
                        if (resultSet.next()) {
                            return resultSet.getString(1);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Not can same deriveClass " + deriveClass, e);
        } finally {
            if (connect != null) {
                try {
                    connect.close();
                } catch (SQLException e) {
                    LOGGER.error("",e);
                }
            }
        }
        return "-1";
    }
}

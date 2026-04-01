class execute {
public static ResultSet execute(String sql, Object... objects) {
		Connection conn = getConnection();
		Statement s = null;
		
		try {
			s = conn.createStatement();
			if (s.execute(sql)) {
				return s.getResultSet();
			} else {
				return null;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (s != null) {
					s.close();
				}
				conn.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}
}

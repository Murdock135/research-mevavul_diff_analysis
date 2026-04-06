class execute {
public static ResultSet execute(String sql, Object... objects) {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		
		try {
			System.out.println(sql);
			ps = conn.prepareStatement(sql);
			
			// Set the parameters
			if (objects != null)
				for (int parameterIndex = 0; parameterIndex < objects.length; ++parameterIndex){
					ps.setObject(parameterIndex + 1, objects[parameterIndex]);
				}
			
			if (ps.execute())
				return ps.getResultSet();
			else
				return null;
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				conn.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}
}

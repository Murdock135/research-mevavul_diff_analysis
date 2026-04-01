class execute {
public void execute(Transaction t) throws SQLException {
			PreparedStatement prepStmt = t.getConnection().prepareStatement(query);
			prepStmt.setString(1, args[0]);
			prepStmt.setString(2, args[1]);
			try {
				ResultSet rs = prepStmt.executeQuery();
				ResultSetMetaData metaData = rs.getMetaData();
				while (rs.next()) {
					Map map = new HashMap();
					for (int i = 0; i < metaData.getColumnCount(); i++) {
						map.put(metaData.getColumnLabel(i + 1), rs.getString(i + 1));
					}
					list.add(map);
				}
	        } finally {
	        	prepStmt.close();
	        }
		}
}

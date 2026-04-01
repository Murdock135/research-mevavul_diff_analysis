class execute {
public void execute(Transaction t) throws SQLException {
			//Statement st = t.getConnection().createStatement();
			Statement st = t.getConnection().prepareStatement(query, variables);
			try {
				ResultSet rs = st.executeQuery(query);
				ResultSetMetaData metaData = rs.getMetaData();
				while (rs.next()) {
					Map map = new HashMap();
					for (int i = 0; i < metaData.getColumnCount(); i++) {
						map.put(metaData.getColumnLabel(i + 1), rs.getString(i + 1));
					}
					list.add(map);
				}
	        } finally {
	            st.close();
	        }
		}
}

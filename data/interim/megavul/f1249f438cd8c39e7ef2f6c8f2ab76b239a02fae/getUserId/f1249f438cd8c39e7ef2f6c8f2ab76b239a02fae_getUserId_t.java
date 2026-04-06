class getUserId {
@Override
	public int getUserId(String user){
		int id = -1;
		Driver driver = new SQLServerDriver();
		try {
			Connection con = driver.connect(connectionUrl, new Properties());
			PreparedStatement statement = con.prepareStatement("Select userId from UserTable where userName = ?");
			statement.setString(1, user);
			ResultSet rs = statement.executeQuery();
			rs.next();
			String storedId = rs.getString("userId");
			id = Integer.parseInt(storedId);
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		return id;
	}
}

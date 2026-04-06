class getUserName {
@Override
	public String getUserName(int userId){
		String userName = null;
		Driver driver = new SQLServerDriver();
		try {
			Connection con = driver.connect(connectionUrl, new Properties());
			PreparedStatement statement = con.prepareStatement("Select userName from UserTable where userId = ?");
			statement.setInt(1, userId);
			ResultSet rs = statement.executeQuery();
			rs.next();
			userName = rs.getString("userName");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		
		return userName;
	}
}

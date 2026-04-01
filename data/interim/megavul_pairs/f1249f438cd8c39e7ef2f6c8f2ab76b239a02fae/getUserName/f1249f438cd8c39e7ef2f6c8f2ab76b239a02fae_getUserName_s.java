class getUserName {
@Override
	public String getUserName(int userId){
		String userName = null;
		Driver driver = new SQLServerDriver();
		String connectionUrl = "jdbc:sqlserver://n8bu1j6855.database.windows.net:1433;database=VoyagerDB;user=VoyageLogin@n8bu1j6855;password={GroupP@ssword};encrypt=true;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
		try {
			Connection con = driver.connect(connectionUrl, new Properties());
			PreparedStatement statement = con.prepareStatement("Select userName from UserTable where userId = '" + userId + "'");
			ResultSet rs = statement.executeQuery();
			rs.next();
			userName = rs.getString("userName");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		
		return userName;
	}
}

class updateUser {
@Override
	public void updateUser(Account user){
		Driver driver = new SQLServerDriver();
		String connectionUrl = "jdbc:sqlserver://n8bu1j6855.database.windows.net:1433;database=VoyagerDB;user=VoyageLogin@n8bu1j6855;password={GroupP@ssword};encrypt=true;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
		try {
			Connection con = driver.connect(connectionUrl, new Properties());
			PreparedStatement statement = con.prepareStatement("UPDATE UserTable "
					+ "SET userPassword='" + user.getPassword() + "', userEmail='" + user.getEmail() + "', userRole='" + user.getRole().toString() + "'"
					+ "WHERE userName='" + user.getUsername() + "'");
			statement.execute();
			System.out.println("Update successful");
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
}

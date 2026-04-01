class removeUser {
@Override
	public void removeUser(Account user){
		Driver driver = new SQLServerDriver();
		String connectionUrl = "jdbc:sqlserver://n8bu1j6855.database.windows.net:1433;database=VoyagerDB;user=VoyageLogin@n8bu1j6855;password={GroupP@ssword};encrypt=true;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
		try {
			Connection con = driver.connect(connectionUrl, new Properties());
			PreparedStatement statement = con.prepareStatement("DELETE FROM UserTable WHERE userName='" + user.getUsername() + "'");
			statement.execute();
			System.out.println("Removal sucessful");
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
}

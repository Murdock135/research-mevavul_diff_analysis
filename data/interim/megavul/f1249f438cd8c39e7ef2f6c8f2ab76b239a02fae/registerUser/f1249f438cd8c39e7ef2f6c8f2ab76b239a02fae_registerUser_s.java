class registerUser {
@Override
	public void registerUser(Account user){
		Driver driver = new SQLServerDriver();
		String connectionUrl = "jdbc:sqlserver://n8bu1j6855.database.windows.net:1433;database=VoyagerDB;user=VoyageLogin@n8bu1j6855;password={GroupP@ssword};encrypt=true;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
		try {
			Connection con = driver.connect(connectionUrl, new Properties());
			PreparedStatement statement = con.prepareStatement("Insert INTO UserTable (userName, userPassword, userEmail, userRole) "
					+ "VALUES ('" + user.getUsername() + "', '" + user.getPassword() + "', '" + user.getEmail() + "', '" + user.getRole().toString() + "');");
			statement.execute();
			System.out.println("Registration Successful");
		} catch (SQLException e) {
			if(e.getMessage().contains("UNIQUE KEY")){
				System.err.println("User has already been registered.");
				throw new UsernameAlreadyExistsException();
			}
			else{
				e.printStackTrace();
			}
		}
	}
}

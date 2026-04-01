class registerUser {
@Override
	public void registerUser(Account user){
		Driver driver = new SQLServerDriver();
		try {
			Connection con = driver.connect(connectionUrl, new Properties());
			PreparedStatement statement = con.prepareStatement("Insert INTO UserTable (userName, userPassword, userEmail, userRole) "
					+ "VALUES (?, ?, ?, ?);");
			statement.setString(1, user.getUsername());
			statement.setString(2, user.getPassword());
			statement.setString(3, user.getEmail());
			statement.setString(4, user.getRole().toString());
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

class login {
@Override
	public Account login(String username, String password){
		Account account = null;
		Driver driver = new SQLServerDriver();
		try {
			Connection con = driver.connect(connectionUrl, new Properties());
			PreparedStatement statement = con.prepareStatement("Select userName, userPassword, userEmail, userRole from UserTable where userName = ?");
			statement.setString(1, username);
			ResultSet rs = statement.executeQuery();
			rs.next();
			String storedPass = rs.getString("userPassword");
			if(storedPass.equals(password)){
				System.out.println("Successfully logged in");
				account = new Account(rs.getString("userName"), rs.getString("userEmail"), "", Roles.valueOf(rs.getString("userRole")), rs.getString("userPassword"));
			}
			else{
				throw new BadLoginException("The username/password combination is incorrect");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			if(e.getMessage().contains("result set has no current row")){
				throw new BadLoginException("The username/password combination is incorrect");
			}
		}	
		
		return account;
	}
}

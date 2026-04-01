class updateUser {
@Override
	public void updateUser(Account user){
		Driver driver = new SQLServerDriver();
		try {
			Connection con = driver.connect(connectionUrl, new Properties());
			PreparedStatement statement = con.prepareStatement("UPDATE UserTable "
					+ "SET userPassword=?, userEmail=?, userRole=?"
					+ "WHERE userName=?");
			statement.setString(1, user.getPassword());
			statement.setString(2, user.getEmail());
			statement.setString(3, user.getRole().toString());
			statement.setString(4, user.getUsername());
			statement.execute();
			System.out.println("Update successful");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

class removeUser {
@Override
	public void removeUser(Account user){
		Driver driver = new SQLServerDriver();
		try {
			Connection con = driver.connect(connectionUrl, new Properties());
			PreparedStatement statement = con.prepareStatement("DELETE FROM UserTable WHERE userName=?");
			statement.setString(1, user.getUsername());
			statement.execute();
			System.out.println("Removal sucessful");
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
}

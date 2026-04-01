class loginValidate {
@Override
	public boolean loginValidate(String userName, String password, String email) {
		String sql = "select * from voter_table where voter_name=? and password=? and email=?";
		try {
			ps = DbUtil.getConnection().prepareStatement(sql);
			ps.setString(1, userName);
			ps.setString(2, password);
			ps.setString(3, email);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return true;
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}

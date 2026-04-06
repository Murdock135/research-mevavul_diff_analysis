class loginValidate {
@Override
	public boolean loginValidate(String userName, String password, String email) {
		String sql = "select * from voter_table where voter_name=? and email=?";
		try {
			ps = DbUtil.getConnection().prepareStatement(sql);
			ps.setString(1, userName);
			ps.setString(2, email);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				String cipherText = rs.getString("password");
				return SHA256.validatePassword(password, cipherText);
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}

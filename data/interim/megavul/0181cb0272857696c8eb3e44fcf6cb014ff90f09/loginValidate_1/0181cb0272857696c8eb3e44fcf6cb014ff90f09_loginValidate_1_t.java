class loginValidate_1 {
@Override
	public boolean loginValidate(String userName, String password) {

		String sql = "select * from admin_table where admin_name=?";
		try {
			ps=DbUtil.getConnection().prepareStatement(sql);
			ps.setString(1, userName);
			ResultSet rs =ps.executeQuery();
			if (rs.next()) {
				String cipherText = rs.getString("password");
				return SHA256.validatePassword(password, cipherText);
			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
}

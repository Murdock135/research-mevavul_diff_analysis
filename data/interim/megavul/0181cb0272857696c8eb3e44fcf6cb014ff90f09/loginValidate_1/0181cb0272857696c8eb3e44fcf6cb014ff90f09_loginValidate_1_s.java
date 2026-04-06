class loginValidate_1 {
@Override
	public boolean loginValidate(String userName, String password) {

		String sql = "select * from admin_table where admin_name=? and password=?";
		try {
			ps=DbUtil.getConnection().prepareStatement(sql);
			ps.setString(1, userName);
			ps.setString(2,password);
			ResultSet rs =ps.executeQuery();
			if (rs.next()) {
				return true;
			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
}

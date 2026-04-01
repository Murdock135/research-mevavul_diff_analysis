class main {
public static void main(String[] args) {
		/////////////////
		// TESTE DE SQL INJECTION
		
		
		String query = "SELECT * FROM USER WHERE NAME = ?";
		ResultSet users = Database.execute(query);
		
		ArrayList<User> searchedUsers = new ArrayList<User>();
		try {
			
			while (users.next()){
				searchedUsers.add(new User(users.getString(1),users.getString(2),users.getString(3)));
			}
			
			Gson gson = new Gson();
			System.out.println(gson.toJson(searchedUsers));

		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
}

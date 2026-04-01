class main {
public static void main(String[] args) {
		/////////////////
		// TESTE DE SQL INJECTION
		try {
			MockDatabase.execute();
			
			
			String query = "{call sp_getUser(?)}";
		
			Object []objs = new Object [1];
			objs[0] = "Jefferson";
			
			ResultSet users = Database.executeStoredPrcedure(query, objs);
		
			ArrayList<User> searchedUsers = new ArrayList<User>();
		
			
			while (users.next()){
				searchedUsers.add(new User(users.getString(1),users.getString(2),users.getString(3)));
			}
			
			Gson gson = new Gson();
			System.out.println(gson.toJson(searchedUsers));

		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
}

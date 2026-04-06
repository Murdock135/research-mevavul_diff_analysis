class doPost {
@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		// Nome dos usuario pesquisado
		String name = req.getParameter("name");
		String[] names = new String[1];
		names[0] = name;
		
		String query = "SELECT * FROM USER WHERE NAME = ?";
		ResultSet users = Database.execute(query,names);
		
		ArrayList<User> searchedUsers = new ArrayList<User>();
		try {
			
			while (users.next()){
				searchedUsers.add(new User(users.getString(1),users.getString(2),users.getString(3)));
			}
			
			Gson gson = new Gson();
			ServletOutputStream os = resp.getOutputStream();
			
			os.print(gson.toJson(searchedUsers));
			os.flush();
			os.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}	
		
	}
}

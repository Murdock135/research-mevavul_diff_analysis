class enterPost {
@Override
	public void enterPost(Post post){
		Driver driver = new SQLServerDriver();
		String connectionUrl = "jdbc:sqlserver://n8bu1j6855.database.windows.net:1433;database=VoyagerDB;user=VoyageLogin@n8bu1j6855;password={GroupP@ssword};encrypt=true;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
		try {
			Connection con = driver.connect(connectionUrl, new Properties());
			PreparedStatement statement = con.prepareStatement("Insert INTO PostTable (postTitle, postAuthorId, postTime, postContent) "
					+ "VALUES ('" + post.getTitle() + "', '" + this.getUserId(post.getAuthor()) + "', CURRENT_TIMESTAMP, '" + post.getMessage() + "');");
			statement.execute();
			System.out.println("Successful post");
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
}

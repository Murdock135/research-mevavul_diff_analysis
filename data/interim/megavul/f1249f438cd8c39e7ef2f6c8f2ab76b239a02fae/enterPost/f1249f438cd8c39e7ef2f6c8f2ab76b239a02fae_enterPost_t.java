class enterPost {
@Override
	public void enterPost(Post post){
		Driver driver = new SQLServerDriver();
		try {
			Connection con = driver.connect(connectionUrl, new Properties());
			PreparedStatement statement = con.prepareStatement("Insert INTO PostTable (postTitle, postAuthorId, postTime, postContent) "
					+ "VALUES ('" + post.getTitle() + "', '" + this.getUserId(post.getAuthor()) + "', CURRENT_TIMESTAMP, '" + post.getMessage() + "');");
			statement.setString(1, post.getTitle());
			statement.setInt(2, this.getUserId(post.getAuthor()));
			statement.setString(3, post.getMessage());
			statement.execute();
			System.out.println("Successful post");
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
}

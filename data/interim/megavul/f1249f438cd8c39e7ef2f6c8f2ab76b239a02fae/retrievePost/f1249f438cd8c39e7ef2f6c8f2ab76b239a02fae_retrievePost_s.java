class retrievePost {
@Override
	public Post retrievePost(String postTitle){
		Post post = null;
		Driver driver = new SQLServerDriver();
		String connectionUrl = "jdbc:sqlserver://n8bu1j6855.database.windows.net:1433;database=VoyagerDB;user=VoyageLogin@n8bu1j6855;password={GroupP@ssword};encrypt=true;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
		try {
			Connection con = driver.connect(connectionUrl, new Properties());
			PreparedStatement statement = con.prepareStatement("Select postTitle, postAuthorId, postTime, postContent from PostTable where postTitle = '" + postTitle + "'");
			ResultSet rs = statement.executeQuery();
			rs.next();
			post = new Post(rs.getString("postTitle"), rs.getString("postContent"), this.getUserName(rs.getInt("postAuthorId")));

		} catch (SQLException e) {
			e.printStackTrace();
		}	
		
		return post;
	}
}

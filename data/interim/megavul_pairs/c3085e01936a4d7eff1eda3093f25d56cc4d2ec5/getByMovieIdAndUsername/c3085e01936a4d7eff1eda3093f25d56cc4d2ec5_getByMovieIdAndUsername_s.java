class getByMovieIdAndUsername {
@Override
    public Review getByMovieIdAndUsername(int movieId, String username) {
        return find.where("movieId = " + movieId + " and username = '" + username + "'").findUnique();
    }
}

class getByMovieIdAndUsername {
@Override
    public Review getByMovieIdAndUsername(int movieId, String username) {
        return find.where().and(Expr.eq("movieId", movieId), Expr.eq("username", username)).findUnique();
    }
}

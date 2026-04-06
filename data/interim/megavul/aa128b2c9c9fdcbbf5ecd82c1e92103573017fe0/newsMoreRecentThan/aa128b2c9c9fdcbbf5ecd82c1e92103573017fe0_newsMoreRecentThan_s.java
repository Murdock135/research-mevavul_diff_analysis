class newsMoreRecentThan {
public List<News> newsMoreRecentThan(String date, String region){
    	Query query = em.createQuery("SELECT n FROM News n WHERE n.region LIKE :r AND n.date > :d ORDER BY date DESC");
    	query.setParameter("r", region);
    	query.setParameter("d", date);
    	
    	@SuppressWarnings("unchecked")
		List<News> news = query.getResultList();
    	
		return news;
    }
}

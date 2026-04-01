class newsMoreRecentThan_1 {
public List<News> newsMoreRecentThan(String date){
    	Query query = em.createQuery("SELECT n FROM News n WHERE n.date > '"+date+"' ORDER BY date DESC");
    	//query.setParameter("d", date);
    	
    	@SuppressWarnings("unchecked")
		List<News> news = query.getResultList();
    	
		return news;
    }
}

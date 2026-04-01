class findAllByEventCrfPath {
public List<ItemDataFlag> findAllByEventCrfPath(int tag_id , String eventCrfPath ) {
    	
        String query = " from " + getDomainClassName() + "  where "
                + " tag_id = :tag_id and path LIKE :eventCrfPath";
        
        org.hibernate.Query q = getCurrentSession().createQuery(query);
        q.setInteger("tag_id", tag_id);
        q.setString("eventCrfPath", eventCrfPath + ".%");
        
        return (List<ItemDataFlag>) q.list();
    }
}

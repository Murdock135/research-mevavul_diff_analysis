class findAllByEventCrfPath {
public List<ItemDataFlag> findAllByEventCrfPath(int tag_id , String eventCrfPath ) {

        String query = " from " + getDomainClassName() + "  where "
                + " tag_id= " + tag_id +  " and path LIKE '" + eventCrfPath +".%'"  ;
        
        org.hibernate.Query q = getCurrentSession().createQuery(query);
        return (List<ItemDataFlag>) q.list();
    }
}

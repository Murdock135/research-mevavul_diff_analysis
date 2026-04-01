class findByItemDataPath {
public ItemDataFlag findByItemDataPath(int tag_id ,  String itemDataPath ) {

        String query = " from " + getDomainClassName() + "  where "
                + " tag_id= " + tag_id  + " and path= '" + itemDataPath +"'"   ;
        
        org.hibernate.Query q = getCurrentSession().createQuery(query);
        return (ItemDataFlag) q.uniqueResult();
    }
}

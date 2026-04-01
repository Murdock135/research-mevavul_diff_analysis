class findByEventCrfPath {
public EventCrfFlag findByEventCrfPath(int tagId, String path) {
        String query = "from " + getDomainClassName() + " where path = :path and tagId= :tagId";
        org.hibernate.Query q = getCurrentSession().createQuery(query);
        q.setInteger("tagId", tagId);
        q.setString("path", path);
        
        return (EventCrfFlag) q.uniqueResult();

    }
}

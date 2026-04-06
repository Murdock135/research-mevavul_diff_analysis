class save {
@Override
   public void save(MapSession session) {
      if (!session.getId().equals(session.getOriginalId())) {
         removeFromCacheWithoutNotifications(session.getOriginalId());
      }
      cache.put(session.getId(), session, session.getMaxInactiveInterval().getSeconds(), TimeUnit.SECONDS);
   }
}

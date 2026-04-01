class save {
@Override
   public void save(MapSession session) {
      if (!session.getId().equals(session.getOriginalId())) {
         deleteById(session.getOriginalId());
      }
      cache.put(session.getId(), session, session.getMaxInactiveInterval().getSeconds(), TimeUnit.SECONDS);
   }
}

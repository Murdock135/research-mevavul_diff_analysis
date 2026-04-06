class save {
@Override
   public void save(MapSession session) {
      cache.put(session.getId(), session, session.getMaxInactiveInterval().getSeconds(), TimeUnit.SECONDS);
   }
}

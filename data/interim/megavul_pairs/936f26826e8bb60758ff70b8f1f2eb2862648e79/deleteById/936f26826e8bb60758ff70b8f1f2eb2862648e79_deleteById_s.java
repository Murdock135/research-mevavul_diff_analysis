class deleteById {
@Override
   public void deleteById(String sessionId) {
      MapSession mapSession = (MapSession) cache.get(sessionId).get();
      if (mapSession != null) {
         applicationEventPublisher.emitSessionDeletedEvent(mapSession);
         cache.evict(sessionId);
      }
   }
}

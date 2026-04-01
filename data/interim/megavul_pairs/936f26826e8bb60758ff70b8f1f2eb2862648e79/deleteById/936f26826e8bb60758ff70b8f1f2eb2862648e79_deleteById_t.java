class deleteById {
@Override
   public void deleteById(String sessionId) {
      ValueWrapper valueWrapper = cache.get(sessionId);
      if (valueWrapper == null) {
         return;
      }
      MapSession mapSession = (MapSession) valueWrapper.get();
      if (mapSession != null) {
         applicationEventPublisher.emitSessionDeletedEvent(mapSession);
         cache.evict(sessionId);
      }
   }
}

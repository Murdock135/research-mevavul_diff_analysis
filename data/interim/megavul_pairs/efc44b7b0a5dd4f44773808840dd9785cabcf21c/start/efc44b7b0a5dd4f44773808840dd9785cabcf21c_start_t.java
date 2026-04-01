class start {
@Override
   public void start() {
      transportFactory = Util.getInstance(configuration.transportFactory());

      if (marshaller == null) {
         marshaller = configuration.marshaller();
         if (marshaller == null) {
            Class<? extends Marshaller> clazz = configuration.marshallerClass();
            if (clazz == GenericJBossMarshaller.class && !configuration.serialWhitelist().isEmpty())
               marshaller = new GenericJBossMarshaller(configuration.serialWhitelist());
            else
               marshaller = Util.getInstance(clazz);
         }
      }

      codec = CodecFactory.getCodec(configuration.version());

      createExecutorService();

      listenerNotifier = ClientListenerNotifier.create(codec, marshaller, transportFactory, configuration.serialWhitelist());
      transportFactory.start(codec, configuration, defaultCacheTopologyId, listenerNotifier,
            asList(listenerNotifier::failoverClientListeners, counterManager));
      counterManager.start(transportFactory, codec, configuration, asyncExecutorService);

      synchronized (cacheName2RemoteCache) {
         for (RemoteCacheHolder rcc : cacheName2RemoteCache.values()) {
            startRemoteCache(rcc);
         }
      }

      // Print version to help figure client version run
      log.version(RemoteCacheManager.class.getPackage().getImplementationVersion());

      warnAboutUberJarDuplicates();

      started = true;
   }
}

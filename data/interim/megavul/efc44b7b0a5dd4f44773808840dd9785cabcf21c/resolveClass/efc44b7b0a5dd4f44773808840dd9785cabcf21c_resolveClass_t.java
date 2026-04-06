class resolveClass {
@Override
      protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
         //Enforce SerialKiller's whitelist
         boolean safeClass = MarshallUtil.isSafeClass(desc.getName(), whitelist);
         if (!safeClass)
            throw log.classNotInWhitelist(desc.getName());

         return super.resolveClass(desc);
      }
}

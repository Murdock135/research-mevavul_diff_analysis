class resolveClass {
@Override
      protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
         //Enforce SerialKiller's whitelist
         boolean safeClass = false;
         for (String whiteRegExp : whitelist) {
            Pattern whitePattern = Pattern.compile(whiteRegExp);
            Matcher whiteMatcher = whitePattern.matcher(desc.getName());
            if (whiteMatcher.find()) {
               safeClass = true;

               if (log.isTraceEnabled())
                  log.tracef("Whitelist match: '%s'", desc.getName());
            }
         }

         if (!safeClass)
            throw log.classNotInWhitelist(desc.getName());

         return super.resolveClass(desc);
      }
}

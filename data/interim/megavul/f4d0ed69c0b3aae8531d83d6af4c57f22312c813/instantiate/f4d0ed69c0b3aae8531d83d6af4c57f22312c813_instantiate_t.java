class instantiate {
public static <T> T instantiate(Class<T> expectedClass, String classname, Properties info,
      boolean tryString,
      @Nullable String stringarg)
      throws ClassNotFoundException, SecurityException, NoSuchMethodException,
          IllegalArgumentException, InstantiationException, IllegalAccessException,
          InvocationTargetException {
    @Nullable Object[] args = {info};
    Constructor<? extends T> ctor = null;
    Class<? extends T> cls = Class.forName(classname).asSubclass(expectedClass);
    try {
      ctor = cls.getConstructor(Properties.class);
    } catch (NoSuchMethodException ignored) {
    }
    if (tryString && ctor == null) {
      try {
        ctor = cls.getConstructor(String.class);
        args = new String[]{stringarg};
      } catch (NoSuchMethodException ignored) {
      }
    }
    if (ctor == null) {
      ctor = cls.getConstructor();
      args = new Object[0];
    }
    return ctor.newInstance(args);
  }
}

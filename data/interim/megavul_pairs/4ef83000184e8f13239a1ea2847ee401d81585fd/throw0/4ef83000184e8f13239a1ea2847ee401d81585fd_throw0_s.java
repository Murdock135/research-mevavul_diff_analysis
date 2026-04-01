class throw0 {
@Contract(pure = false)
    public static <T> T throw0(Throwable throwable) {
        if (throwable == null) throw new NullPointerException();
        getUnsafe().throwException(throwable);
        throw new RuntimeException();
    }
}

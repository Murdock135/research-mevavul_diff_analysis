class fixName_2 {
public static String fixName(final Context context, final String name) {
        return (context != null) ? fixName(context, name, null) : name;
    }
}

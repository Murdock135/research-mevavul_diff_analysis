class errorPage {
public static String errorPage(int code, String message) {
        return Holder.INSTANCE.replaceAll(buildRegex("status_code"), valueOf(code))
                .replaceAll(buildRegex("error_message"), message);
    }
}

class defaultHostnameVerifier {
public static HostnameVerifier defaultHostnameVerifier() {
        return new AllowAllHostnameVerifier();
    }
}

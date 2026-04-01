class builder {
public static WebClient.Builder builder(HttpClient httpClient) {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(makeSafeHttpClient(httpClient)));
    }
}

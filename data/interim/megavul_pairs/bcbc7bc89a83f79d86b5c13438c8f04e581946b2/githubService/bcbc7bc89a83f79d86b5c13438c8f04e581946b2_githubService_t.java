class githubService {
@Bean
  public GithubService githubService(
      Endpoint githubEndpoint, Client retrofitClient, RestAdapter.LogLevel retrofitLogLevel) {
    log.info("Github service loaded");

    GithubService githubClient =
        new RestAdapter.Builder()
            .setEndpoint(githubEndpoint)
            .setConverter(new JacksonConverter())
            .setClient(retrofitClient)
            .setLogLevel(retrofitLogLevel != null ? retrofitLogLevel : RestAdapter.LogLevel.BASIC)
            .setLog(new Slf4jRetrofitLogger(GithubService.class))
            .build()
            .create(GithubService.class);

    return githubClient;
  }
}

class doGet {
@Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

        final String cacheName = ParamUtils.getStringParameter(request, "cacheName", "").trim();
        final Optional<Cache<?, ?>> optionalCache = Arrays.stream(CacheFactory.getAllCaches())
            .filter(cache -> cacheName.equals(cache.getName()))
            .findAny()
            .map(cache -> (Cache<?, ?>) cache);

        if (!optionalCache.isPresent()) {
            request.setAttribute("warningMessage", LocaleUtils.getLocalizedString("system.cache-details.cache_not_found", Collections.singletonList(StringUtils.escapeHTMLTags(cacheName))));
        }

        final boolean secretKey = optionalCache.map(Cache::isKeySecret).orElse(Boolean.FALSE);
        final boolean secretValue = optionalCache.map(Cache::isValueSecret).orElse(Boolean.FALSE);

        final List<Map.Entry<String, String>> cacheEntries = optionalCache.map(Cache::entrySet)
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .map(entry -> new AbstractMap.SimpleEntry<>(secretKey ? "************" : entry.getKey().toString(), secretValue ? "************" : entry.getValue().toString()))
            .sorted(Comparator.comparing(Map.Entry::getKey))
            .collect(Collectors.toList());

        // Find what we're searching for
        final Search search = new Search(request);
        Predicate<Map.Entry<String, String>> predicate = entry -> true;
        if (!search.key.isEmpty() && !secretKey) {
            predicate = predicate.and(entry -> StringUtils.containsIgnoringCase(entry.getKey(), search.key));
        }
        if (!search.value.isEmpty() && !secretValue) {
            predicate = predicate.and(entry -> StringUtils.containsIgnoringCase(entry.getValue(), search.value));
        }

        final ListPager<Map.Entry<String, String>> listPager = new ListPager<>(request, response, cacheEntries, predicate, SEARCH_FIELDS);

        final String csrf = StringUtils.randomString(16);
        CookieUtils.setCookie(request, response, "csrf", csrf, -1);
        addSessionFlashes(request, "errorMessage", "warningMessage", "successMessage");
        request.setAttribute("csrf", csrf);
        request.setAttribute("cacheName", cacheName);
        request.setAttribute("listPager", listPager);
        request.setAttribute("search", search);
        request.getRequestDispatcher("system-cache-details.jsp").forward(request, response);
    }
}

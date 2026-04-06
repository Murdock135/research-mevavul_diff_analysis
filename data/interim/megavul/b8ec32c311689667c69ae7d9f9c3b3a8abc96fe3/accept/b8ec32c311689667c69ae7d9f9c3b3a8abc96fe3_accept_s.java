class accept {
default List<MediaType> accept() {
        return getAll(HttpHeaders.ACCEPT)
            .stream()
            .flatMap(x -> Arrays.stream(x.split(",")))
            .flatMap(s -> ConversionService.SHARED.convert(s, MediaType.class).map(Stream::of).orElse(Stream.empty()))
            .distinct()
            .collect(Collectors.toList());
    }
}

class getSortedIdProviders {
private IdProviders getSortedIdProviders()
    {
        IdProviders idProviders = securityService.get().getIdProviders();
        return IdProviders.from(
            idProviders.stream().sorted( Comparator.comparing( u -> u.getKey().toString() ) ).collect( Collectors.toList() ) );
    }
}

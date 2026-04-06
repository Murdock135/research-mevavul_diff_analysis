class getProgramUidsFilter {
private String getProgramUidsFilter( Set<String> programUids )
    {
        return "pr.uid in (" +
            programUids.stream()
                .map( this::withQuotes )
                .collect( joining( "," ) )
            + ")";
    }
}

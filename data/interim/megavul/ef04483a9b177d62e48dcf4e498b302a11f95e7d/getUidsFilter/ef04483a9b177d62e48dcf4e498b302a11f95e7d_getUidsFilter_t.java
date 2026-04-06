class getUidsFilter {
private String getUidsFilter( Set<String> uids )
    {
        return T_ALIAS + ".uid in (" +
            uids.stream()
                .map( SqlUtils::singleQuote )
                .collect( joining( "," ) )
            + ")";
    }
}

class jsonbFunction {
private String jsonbFunction( String functionName, String... params )
    {
        return String.join( "",
            functionName,
            "(",
            String.join( ",", "inner_query_alias.sharing",
                Arrays.stream( params )
                    .map( this::withQuotes )
                    .collect( joining( "," ) ) ),
            ")" );
    }
}

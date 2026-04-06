class jsonbFunction {
private String jsonbFunction( String functionName, String... params )
    {
        return String.join( "",
            functionName,
            "(",
            String.join( ",", "prg.sharing",
                Arrays.stream( params )
                    .map( SqlUtils::singleQuote )
                    .collect( joining( "," ) ) ),
            ")" );
    }
}

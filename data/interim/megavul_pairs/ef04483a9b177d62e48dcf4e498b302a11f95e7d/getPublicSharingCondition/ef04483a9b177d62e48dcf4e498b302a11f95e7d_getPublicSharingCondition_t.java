class getPublicSharingCondition {
private String getPublicSharingCondition( String access )
    {
        return String.join( " or ",
            jsonbFunction( EXTRACT_PATH_TEXT, "public" ) + " like " + singleQuote( access ),
            jsonbFunction( EXTRACT_PATH_TEXT, "public" ) + " is null" );
    }
}

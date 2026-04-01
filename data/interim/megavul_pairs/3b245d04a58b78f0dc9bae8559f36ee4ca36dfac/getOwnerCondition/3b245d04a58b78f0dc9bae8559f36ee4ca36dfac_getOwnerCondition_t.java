class getOwnerCondition {
private String getOwnerCondition( CurrentUserGroupInfo currentUserGroupInfo )
    {
        return String.join( " or ",
            jsonbFunction( EXTRACT_PATH_TEXT, "owner" ) + " = " + singleQuote( currentUserGroupInfo.getUserUID() ),
            jsonbFunction( EXTRACT_PATH_TEXT, "owner" ) + " is null" );
    }
}

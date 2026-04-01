class getOwnerCondition {
private String getOwnerCondition( CurrentUserGroupInfo currentUserGroupInfo )
    {
        return String.join( " or ",
            jsonbFunction( EXTRACT_PATH_TEXT, "owner" ) + " = " + withQuotes( currentUserGroupInfo.getUserUID() ),
            jsonbFunction( EXTRACT_PATH_TEXT, "owner" ) + " is null" );
    }
}

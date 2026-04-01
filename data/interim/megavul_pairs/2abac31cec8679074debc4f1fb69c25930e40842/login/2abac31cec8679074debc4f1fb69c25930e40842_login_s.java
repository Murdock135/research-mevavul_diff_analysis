class login {
public LoginResultMapper login()
    {
        AuthenticationInfo authInfo = noIdProviderSpecified() ? attemptLoginWithAllExistingIdProviders() : attemptLogin();

        if ( authInfo.isAuthenticated() )
        {
            switch ( this.scope )
            {
                case REQUEST:
                    this.context.get().getLocalScope().setAttribute( authInfo );
                    break;
                case SESSION:
                default:
                    createSession( authInfo );
                    break;
            }

            return new LoginResultMapper( authInfo );
        }
        else
        {
            return new LoginResultMapper( authInfo, "Access Denied" );
        }
    }
}

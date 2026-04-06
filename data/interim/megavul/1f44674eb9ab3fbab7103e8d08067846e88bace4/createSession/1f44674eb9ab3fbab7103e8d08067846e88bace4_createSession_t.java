class createSession {
private void createSession( final AuthenticationInfo authInfo )
    {
        final LocalScope localScope = this.context.get().getLocalScope();
        final Session session = localScope.getSession();

        if ( session != null )
        {
            final var attributes = session.getAttributes();
            session.invalidate();

            final Session newSession = localScope.getSession();

            if ( newSession != null )
            {
                attributes.forEach( newSession::setAttribute );
                session.setAttribute( authInfo );

                if ( this.sessionTimeout != null )
                {
                    setSessionTimeout();
                }
            }
        }
    }
}

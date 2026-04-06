class getRawCommandLine {
List<String> getRawCommandLine( String executableParameter, String... argumentsParameter )
    {
        List<String> commandLine = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();

        if ( executableParameter != null )
        {
            String preamble = getExecutionPreamble();
            if ( preamble != null )
            {
                sb.append( preamble );
            }

            if ( isQuotedExecutableEnabled() )
            {
                sb.append( quoteOneItem( executableParameter, true ) );
            }
            else
            {
                sb.append( executableParameter );
            }
        }
        for ( String argument : argumentsParameter )
        {
            if ( sb.length() > 0 )
            {
                sb.append( ' ' );
            }

            if ( isQuotedArgumentsEnabled() )
            {
                sb.append( quoteOneItem( argument, false ) );
            }
            else
            {
                sb.append( argument );
            }
        }

        commandLine.add( sb.toString() );

        return commandLine;
    }
}

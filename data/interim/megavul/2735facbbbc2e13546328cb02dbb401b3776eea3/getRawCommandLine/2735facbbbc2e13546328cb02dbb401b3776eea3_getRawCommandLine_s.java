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
                char[] escapeChars =
                    getEscapeChars( isSingleQuotedExecutableEscaped(), isDoubleQuotedExecutableEscaped() );

                sb.append( StringUtils.quoteAndEscape( getExecutable(), getExecutableQuoteDelimiter(), escapeChars,
                                                       getQuotingTriggerChars(), '\\', false ) );
            }
            else
            {
                sb.append( getExecutable() );
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
                char[] escapeChars = getEscapeChars( isSingleQuotedArgumentEscaped(), isDoubleQuotedArgumentEscaped() );

                sb.append( StringUtils.quoteAndEscape( argument, getArgumentQuoteDelimiter(), escapeChars,
                                                       getQuotingTriggerChars(), '\\', false ) );
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

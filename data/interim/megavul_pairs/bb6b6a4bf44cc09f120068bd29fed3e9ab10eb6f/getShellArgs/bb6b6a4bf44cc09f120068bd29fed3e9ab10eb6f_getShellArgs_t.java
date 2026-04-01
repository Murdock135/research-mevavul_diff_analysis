class getShellArgs {
String[] getShellArgs()
    {
        if ( shellArgs.isEmpty() )
        {
            return null;
        }
        else
        {
            return shellArgs.toArray( new String[0] );
        }
    }
}

class getShellArgs {
String[] getShellArgs()
    {
        if ( ( shellArgs == null ) || shellArgs.isEmpty() )
        {
            return null;
        }
        else
        {
            return shellArgs.toArray( new String[shellArgs.size()] );
        }
    }
}

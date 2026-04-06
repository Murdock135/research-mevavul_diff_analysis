class quoteOneItem {
protected String quoteOneItem( String path, boolean isExecutable )
    {
        if ( path == null )
        {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append( "'" );
        sb.append( path.replace( "'", "'\"'\"'" ) );
        sb.append( "'" );
        return sb.toString();
    }
}

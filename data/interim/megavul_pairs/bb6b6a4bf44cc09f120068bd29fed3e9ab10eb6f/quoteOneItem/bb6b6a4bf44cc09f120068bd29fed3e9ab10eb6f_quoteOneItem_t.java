class quoteOneItem {
protected String quoteOneItem( String path, boolean isExecutable )
    {
        if ( path == null )
        {
            return null;
        }

        return "'" + path.replace( "'", "'\"'\"'" ) + "'";
    }
}

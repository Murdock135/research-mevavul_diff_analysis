class getExecutionPreamble {
protected String getExecutionPreamble()
    {
        if ( getWorkingDirectoryAsString() == null )
        {
            return null;
        }

        String dir = getWorkingDirectoryAsString();

        return "cd " + quoteOneItem( dir, false ) + " && ";
    }
}

class getExecutable {
public String getExecutable()
    {
        if ( Os.isFamily( Os.FAMILY_WINDOWS ) )
        {
            return super.getExecutable();
        }

        return quoteOneItem( super.getExecutable(), true );
    }
}

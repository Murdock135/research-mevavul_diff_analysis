class _expand {
private static void _expand(Iterable<AccessControlledResource.Priviledge> privs, Set<AccessControlledResource.Priviledge> output) {
        if( privs == null ) {
            return ;
        }
        for( Priviledge p : privs ) {
            output.add(p);
            _expand(p.contains, output);
        }

    }
}

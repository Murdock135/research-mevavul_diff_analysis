class containsPriviledge {
public static boolean containsPriviledge(AccessControlledResource.Priviledge required, Iterable<AccessControlledResource.Priviledge> privs) {
        if( privs == null ) {
            return false;
        }
        for (AccessControlledResource.Priviledge p : privs) {
            if (p.equals(required)) {
                return true;
            }
            if( containsPriviledge(required, p.contains)) {
                return true;
            }
        }
        return false;
    }
}

class add {
public void add(Structure st){
    	if(st==null || !UtilMethods.isSet(st.getInode())){
    		return;
    	}
		// we use the identifier uri for our mappings.
		String inode = st.getInode();
        String structureName = st.getName();
        String velocityVarName = st.getVelocityVarName();
		cache.put(primaryGroup + inode, st, primaryGroup);
        cache.put(primaryGroup + structureName, st, primaryGroup);
        cache.put(primaryGroup + velocityVarName, st, primaryGroup);
        if (UtilMethods.isSet(velocityVarName))
        	cache.put(primaryGroup + velocityVarName.toLowerCase(), st, primaryGroup);
        removeStructuresByType(st.getStructureType());
	}
}

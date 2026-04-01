class getStructureByVelocityVarName {
@SuppressWarnings("unchecked")
	public static Structure getStructureByVelocityVarName(String varName)
	{
		if(varName ==null) return new Structure();
		Structure structure = null;
		String condition = " lower(velocity_var_name) = '" + varName.toLowerCase() + "'";
		List<Structure> list = InodeFactory.getInodesOfClassByCondition(Structure.class,condition);
		if (list.size() > 0)
		{
			structure = (Structure) list.get(0);
		}
		else
		{
			structure = new Structure();
		}
		return structure;
	}
}

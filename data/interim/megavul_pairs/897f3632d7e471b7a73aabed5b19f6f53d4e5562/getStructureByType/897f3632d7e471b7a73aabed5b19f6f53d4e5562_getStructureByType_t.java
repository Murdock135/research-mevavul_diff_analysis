class getStructureByType {
public static Structure getStructureByType(String type)
	{
		type = SQLUtil.sanitizeParameter(type);
		Structure structure = null;
		String condition = " name = '" + type + "'";
		List list = InodeFactory.getInodesOfClassByCondition(Structure.class,condition);
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

class revertLogicDeleted {
@Override
	@CacheEvict(value={CacheConstant.SYS_USERS_CACHE}, allEntries=true)
	public boolean revertLogicDeleted(List<String> userIds, SysUser updateEntity) {
		String ids = String.format("'%s'", String.join("','", userIds));
		return userMapper.revertLogicDeleted(ids, updateEntity) > 0;
	}
}

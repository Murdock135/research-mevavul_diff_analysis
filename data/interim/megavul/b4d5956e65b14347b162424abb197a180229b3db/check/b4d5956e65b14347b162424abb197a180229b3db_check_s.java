class check {
@SuppressWarnings("unchecked")
    public List<CmsContent> check(short siteId, SysUser user, Serializable[] ids) {
        List<CmsContent> entityList = new ArrayList<>();
        for (CmsContent entity : getEntitys(ids)) {
            if (null != entity && siteId == entity.getSiteId() && STATUS_PEND == entity.getStatus()
                    && (user.isOwnsAllContent() || entity.getUserId() == user.getId())) {
                entity.setStatus(STATUS_NORMAL);
                entity.setCheckUserId(user.getId());
                entity.setCheckDate(CommonUtils.getDate());
                for (CmsContent quote : (List<CmsContent>) getPage(new CmsContentQuery(siteId, null, null, null, null, null, null,
                        null, entity.getId(), null, null, null, null, null, null, null, null, null, null), null, null, null, null,
                        null).getList()) {
                    quote.setStatus(STATUS_NORMAL);
                }
                entityList.add(entity);
            }
        }
        return entityList;
    }
}

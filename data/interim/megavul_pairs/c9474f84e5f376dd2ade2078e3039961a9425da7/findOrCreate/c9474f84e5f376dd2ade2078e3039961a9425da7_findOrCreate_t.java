class findOrCreate {
protected ID findOrCreate(String name, String code, ID parent, int level) {
        String sql = "select itemId from ClassificationData where dataId = ? and ";
        if (StringUtils.isNotBlank(code)) {
            sql += String.format("(code = '%s' or name = '%s')",
                    CommonsUtils.escapeSql(code), CommonsUtils.escapeSql(name));
        } else {
            sql += String.format("name = '%s'", CommonsUtils.escapeSql(name));
        }

        if (parent != null) {
            sql += String.format(" and parent = '%s'", parent);
        }

        Object[] exists = Application.createQueryNoFilter(sql).setParameter(1, dest).unique();
        if (exists != null) {
            return (ID) exists[0];
        }

        Record item = EntityHelper.forNew(EntityHelper.ClassificationData, this.getUser());
        item.setString("name", name);
        item.setInt("level", level);
        item.setID("dataId", dest);
        if (StringUtils.isNotBlank(code)) {
            item.setString("code", code);
        }
        if (parent != null) {
            item.setID("parent", parent);
        }

        item = Application.getBean(ClassificationService.class).createOrUpdateItem(item);
        this.addSucceeded();
        return item.getPrimary();
    }
}

class checkoutReferenceValue {
protected ID checkoutReferenceValue(Field field, Cell cell) {
        final String val = cell.asString();
        final Entity refEntity = field.getReferenceEntity();

        // 支持ID
        ID vla2id = MetadataHelper.checkSpecEntityId(val, refEntity.getEntityCode());
        if (vla2id != null) {
            if (QueryHelper.exists(vla2id)) return vla2id;

            log.warn("Reference ID `{}` not exists", vla2id);
            return null;
        }

        Object val2Text = checkoutFieldValue(refEntity.getNameField(), cell, false);
        if (val2Text == null) return null;

        Query query;
        // 用户特殊处理
        if (refEntity.getEntityCode() == EntityHelper.User) {
            String sql = MessageFormat.format(
                    "select userId from User where loginName = ''{0}'' or email = ''{0}'' or fullName = ''{0}''",
                    CommonsUtils.escapeSql(val2Text.toString()));
            query = Application.createQueryNoFilter(sql);
        } else {
            // 查找引用实体的名称字段和自动编号字段
            Set<String> queryFields = new HashSet<>();
            queryFields.add(refEntity.getNameField().getName());
            // 名称字段又是引用字段
            if (!(val2Text instanceof ID)) {
                for (Field s : MetadataSorter.sortFields(refEntity, DisplayType.SERIES)) {
                    queryFields.add(s.getName());
                }
            }

            StringBuilder sql = new StringBuilder(
                    String.format("select %s from %s where ", refEntity.getPrimaryField().getName(), refEntity.getName()));
            for (String qf : queryFields) {
                sql.append(
                        String.format("%s = '%s' or ", qf, CommonsUtils.escapeSql(val2Text.toString())));
            }
            sql = new StringBuilder(sql.substring(0, sql.length() - 4));

            query = Application.createQueryNoFilter(sql.toString());
        }

        Object[] found = query.unique();
        return found != null ? (ID) found[0] : null;
    }
}

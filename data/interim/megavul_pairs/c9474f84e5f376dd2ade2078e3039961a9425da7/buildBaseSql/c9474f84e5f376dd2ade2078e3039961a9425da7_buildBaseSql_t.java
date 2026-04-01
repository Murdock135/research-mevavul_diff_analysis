class buildBaseSql {
private String buildBaseSql(ID mainid, String relatedExpr, String q, boolean count, ID user) {
        // format: Entity.Field
        Entity relatedEntity = MetadataHelper.getEntity(relatedExpr.split("\\.")[0]);

        String where = new ProtocolFilterParser(null).parseRelated(relatedExpr, mainid);

        // @see FeedsListController#fetchFeeds
        if (relatedEntity.getEntityCode() == EntityHelper.Feeds) {
            where += String.format(" and (type = %d or type = %d)",
                    FeedsType.FOLLOWUP.getMask(),
                    FeedsType.SCHEDULE.getMask());

            List<String> in = new ArrayList<>();
            in.add("scope = 'ALL'");
            for (Team t : Application.getUserStore().getUser(user).getOwningTeams()) {
                in.add(String.format("scope = '%s'", t.getIdentity()));
            }
            where += " and ( " + StringUtils.join(in, " or ") + " )";
        }

        if (StringUtils.isNotBlank(q)) {
            Set<String> searchFields = ParseHelper.buildQuickFields(relatedEntity, null);

            if (!searchFields.isEmpty()) {
                String like = " like '%" + CommonsUtils.escapeSql(q) + "%'";
                String searchWhere = " and ( " + StringUtils.join(searchFields.iterator(), like + " or ") + like + " )";
                where += searchWhere;
            }
        }

        Field primaryField = relatedEntity.getPrimaryField();
        Field namedField = relatedEntity.getNameField();

        StringBuilder sql = new StringBuilder("select ");
        if (count) {
            sql.append("count(").append(primaryField.getName()).append(")");
        } else {
            sql.append(primaryField.getName()).append(",")
                    .append(namedField.getName()).append(",")
                    .append(EntityHelper.ModifiedOn);

            if (MetadataHelper.hasApprovalField(relatedEntity)) {
                sql.append(",").append(EntityHelper.ApprovalState);
            }
        }

        sql.append(" from ").append(relatedEntity.getName()).append(" where ").append(where);
        return sql.toString();
    }
}

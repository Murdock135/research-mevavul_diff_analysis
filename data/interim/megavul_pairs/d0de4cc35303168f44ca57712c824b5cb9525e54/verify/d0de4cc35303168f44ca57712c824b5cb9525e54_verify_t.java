class verify {
@Override
    public void verify(Record record) {
        // 自动只读字段忽略非空检查
        final Set<String> autoReadonlyFields = EasyMetaFactory.getAutoReadonlyFields(entity.getName());

        List<String> notNulls = new ArrayList<>();  // 非空
        List<String> notWells = new ArrayList<>();  // 格式

        // 新建
        if (record.getPrimary() == null) {
            for (Field field : entity.getFields()) {
                if (MetadataHelper.isCommonsField(field)) continue;

                EasyField easyField = EasyMetaFactory.valueOf(field);
                if (easyField.getDisplayType() == DisplayType.SERIES
                        || easyField.getDisplayType() == DisplayType.BARCODE) {
                    continue;
                }

                Object hasVal = record.getObjectValue(field.getName());
                boolean canNull = field.isNullable() || autoReadonlyFields.contains(field.getName());

                if (NullValue.isNull(hasVal)) {
                    if (!canNull) {
                        notNulls.add(easyField.getLabel());
                    }
                } else {
                    if (field.isCreatable()) {
                        if (!patternMatches(easyField, hasVal)) {
                            notWells.add(easyField.getLabel());
                        }
                    } else {
                        if (!isForceCreateable(field)) {
                            log.warn("Remove non-creatable field : {}", field);
                            record.removeValue(field.getName());
                        }
                    }
                }
            }
        }
        // 更新
        else {
            for (String fieldName : record.getAvailableFields()) {
                Field field = entity.getField(fieldName);
                if (MetadataHelper.isCommonsField(field)) continue;

                Object hasVal = record.getObjectValue(field.getName());
                boolean canNull = field.isNullable() || autoReadonlyFields.contains(field.getName());

                EasyField easyField = EasyMetaFactory.valueOf(field);
                if (NullValue.isNull(hasVal)) {
                    if (!canNull) {
                        notNulls.add(easyField.getLabel());
                    }
                } else {
                    if (field.isUpdatable()) {
                        if (!patternMatches(easyField, hasVal)) {
                            notWells.add(easyField.getLabel());
                        }
                    } else {
                        log.warn("Remove non-updatable field : {}", field);
                        record.removeValue(fieldName);
                    }
                }
            }
        }

        if (!notNulls.isEmpty()) {
            throw new DataSpecificationException(
                    Language.L("%s 不允许为空", StringUtils.join(notNulls, " / ")));
        }
        if (!notWells.isEmpty()) {
            throw new DataSpecificationException(
                    Language.L("%s 格式不正确", StringUtils.join(notWells, " / ")));
        }

        removeFieldIfSafeCheck(record);

        // TODO 检查引用字段的ID是否正确（是否是其他实体的ID）

    }
}

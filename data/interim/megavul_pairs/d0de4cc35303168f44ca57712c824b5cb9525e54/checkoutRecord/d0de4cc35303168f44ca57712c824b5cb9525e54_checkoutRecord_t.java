class checkoutRecord {
protected Record checkoutRecord(Cell[] row, ID defaultOwning) {
        Record recordHub = EntityHelper.forNew(rule.getToEntity().getEntityCode(), defaultOwning);

        // 解析数据
        RecordCheckout recordCheckout = new RecordCheckout(rule.getFiledsMapping());
        Record checkout = recordCheckout.checkout(recordHub, row);

        if (recordCheckout.getTraceLogs().isEmpty()) {
            cellTraces = null;
        } else {
            cellTraces = StringUtils.join(recordCheckout.getTraceLogs(), ", ");
        }

        // 检查重复
        if (rule.getRepeatOpt() < ImportRule.REPEAT_OPT_IGNORE) {
            final ID repeat = findRepeatedRecordId(rule.getRepeatFields(), recordHub);

            if (repeat != null && rule.getRepeatOpt() == ImportRule.REPEAT_OPT_SKIP) {
                return null;
            }

            if (repeat != null && rule.getRepeatOpt() == ImportRule.REPEAT_OPT_UPDATE) {
                // 更新
                checkout = EntityHelper.forUpdate(repeat, defaultOwning);
                for (Iterator<String> iter = recordHub.getAvailableFieldIterator(); iter.hasNext(); ) {
                    String field = iter.next();
                    if (MetadataHelper.isCommonsField(field)) continue;

                    checkout.setObjectValue(field, recordHub.getObjectValue(field));
                }
            }
        }

        // Verify new record
        // Throws DataSpecificationException
        if (checkout.getPrimary() == null) {
            new EntityRecordCreator(rule.getToEntity(), JSONUtils.EMPTY_OBJECT, null, Boolean.FALSE)
                    .verify(checkout);
        }

        return checkout;
    }
}

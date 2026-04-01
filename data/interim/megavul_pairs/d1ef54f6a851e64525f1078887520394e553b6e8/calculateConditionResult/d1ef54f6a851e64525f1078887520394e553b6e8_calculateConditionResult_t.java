class calculateConditionResult {
private DependResult calculateConditionResult() {
        DependResult conditionResult = DependResult.SUCCESS;

        List<SwitchResultVo> switchResultVos = taskParameters.getDependTaskList();

        SwitchResultVo switchResultVo = new SwitchResultVo();
        switchResultVo.setNextNode(taskParameters.getNextNode());
        switchResultVos.add(switchResultVo);
        // todo: refactor these calculate code
        int finalConditionLocation = switchResultVos.size() - 1;
        int i = 0;

        Map<String, Property> globalParams = JSONUtils
                .toList(processInstance.getGlobalParams(), Property.class)
                .stream()
                .collect(Collectors.toMap(Property::getProp, Property -> Property));
        Map<String, Property> varParams = JSONUtils
                .toList(taskInstance.getVarPool(), Property.class)
                .stream()
                .collect(Collectors.toMap(Property::getProp, Property -> Property));

        for (SwitchResultVo info : switchResultVos) {
            log.info("Begin to execute {} condition: {} ", (i + 1), info.getCondition());
            if (StringUtils.isEmpty(info.getCondition())) {
                finalConditionLocation = i;
                break;
            }
            String content =
                    SwitchTaskUtils.generateContentWithTaskParams(info.getCondition(), globalParams, varParams);
            log.info("Format condition sentence::{} successfully", content);
            Boolean result;
            try {
                result = SwitchTaskUtils.evaluate(content);
                log.info("Execute condition sentence: {} successfully: {}", content, result);
            } catch (Exception e) {
                log.info("Execute condition sentence: {} failed", content, e);
                conditionResult = DependResult.FAILED;
                break;
            }
            if (result) {
                finalConditionLocation = i;
                break;
            }
            i++;
        }
        taskParameters.setDependTaskList(switchResultVos);
        taskParameters.setResultConditionLocation(finalConditionLocation);
        taskInstance.setSwitchDependency(taskParameters);

        if (!isValidSwitchResult(switchResultVos.get(finalConditionLocation))) {
            conditionResult = DependResult.FAILED;
            log.error("The switch task depend result is invalid, result:{}, switch branch:{}", conditionResult,
                    finalConditionLocation);
        }

        log.info("The switch task depend result:{}, switch branch:{}", conditionResult, finalConditionLocation);
        return conditionResult;
    }
}

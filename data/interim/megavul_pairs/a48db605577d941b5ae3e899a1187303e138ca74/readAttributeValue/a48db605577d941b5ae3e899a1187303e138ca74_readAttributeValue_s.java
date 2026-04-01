class readAttributeValue {
private ModelNode readAttributeValue(PathAddress address, String attributeName) {
        final ModelNode readAttributeOp = new ModelNode();
        readAttributeOp.get(OP).set(READ_ATTRIBUTE_OPERATION);
        readAttributeOp.get(OP_ADDR).set(address.toModelNode());
        readAttributeOp.get(ModelDescriptionConstants.INCLUDE_UNDEFINED_METRIC_VALUES).set(false);
        readAttributeOp.get(NAME).set(attributeName);
        ModelNode response = modelControllerClient.execute(readAttributeOp);
        String error = getFailureDescription(response);
        if (error != null) {
            // [WFLY-11933] if the value can not be read if the management resource is not accessible due to RBAC,
            // it is logged it at a lower level.
            if (error.contains("WFLYCTL0216")) {
                LOGGER.debugf("Unable to read attribute %s: %s.", attributeName, error);
            } else{
                LOGGER.unableToReadAttribute(attributeName, address, error);
            }
            return new ModelNode(ModelType.UNDEFINED);
        }
        return  response.get(RESULT);
    }
}

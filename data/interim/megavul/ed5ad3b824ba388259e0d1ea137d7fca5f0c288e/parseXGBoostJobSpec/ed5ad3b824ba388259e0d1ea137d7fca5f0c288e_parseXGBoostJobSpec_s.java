class parseXGBoostJobSpec {
private XGBoostJobSpec parseXGBoostJobSpec(ExperimentSpec experimentSpec)
          throws InvalidSpecException {
    XGBoostJobSpec xGBoostJobSpec = new XGBoostJobSpec();

    Map<XGBoostJobReplicaType, MLJobReplicaSpec> replicaSpecMap = new HashMap<>();

    for (Map.Entry<String, ExperimentTaskSpec> entry : experimentSpec.getSpec().entrySet()) {
      String replicaType = entry.getKey();
      ExperimentTaskSpec taskSpec = entry.getValue();
      V1Container initContainer = this.getExperimentHandlerContainer(experimentSpec);
      if (XGBoostJobReplicaType.isSupportedReplicaType(replicaType)) {
        MLJobReplicaSpec replicaSpec = new MLJobReplicaSpec();
        replicaSpec.setReplicas(taskSpec.getReplicas());
        V1PodTemplateSpec podTemplateSpec = ExperimentSpecParser.parseTemplateSpec(taskSpec, experimentSpec);
        
        if (initContainer != null && replicaType.equals("Master")) {
          podTemplateSpec.getSpec().addInitContainersItem(initContainer);  
        }
        
        replicaSpec.setTemplate(podTemplateSpec);
        replicaSpecMap.put(XGBoostJobReplicaType.valueOf(replicaType), replicaSpec);
      } else {
        throw new InvalidSpecException("Unrecognized replica type name: " +
            entry.getKey() +
            ", it should be " +
            String.join(",", XGBoostJobReplicaType.names()) +
            " for XGBoost experiment.");
      }
    }
    xGBoostJobSpec.setReplicaSpecs(replicaSpecMap);
    return xGBoostJobSpec;
  }
}

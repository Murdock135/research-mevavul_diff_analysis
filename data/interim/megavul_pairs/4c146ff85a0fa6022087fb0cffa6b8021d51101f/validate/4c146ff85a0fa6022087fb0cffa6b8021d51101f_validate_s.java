class validate {
private void validate() {
		if (allowedThreadCount.orElse(0) < 0)
			throw new ConfigurationException(localized("security.configuration_invalid_negative_threads")); //$NON-NLS-1$
		if (!Collections.disjoint(allowedLocalPorts, excludedLocalPorts))
			throw new ConfigurationException(localized("security.configuration_invalid_port_rule_intersection")); //$NON-NLS-1$
		allowedLocalPorts.forEach(ArtemisSecurityConfigurationBuilder::validatePortRange);
		excludedLocalPorts.forEach(ArtemisSecurityConfigurationBuilder::validatePortRange);
		allowLocalPortsAbove.ifPresent(value -> {
			validatePortRange(value);
			if (allowedLocalPorts.stream().anyMatch(allowed -> allowed > value))
				throw new ConfigurationException(localized("security.configuration_invalid_port_allowed_in_rage")); //$NON-NLS-1$
			if (excludedLocalPorts.stream().anyMatch(exclusion -> exclusion <= value))
				throw new ConfigurationException(localized("security.configuration_invalid_port_exclude_outside_rage")); //$NON-NLS-1$
		});
	}
}

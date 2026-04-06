class getOrderClause {
private static String getOrderClause(Set<String> joinAliases, Set<String> functionAlias, String alias, Order order) {

		String property = order.getProperty();

		checkSortExpression(order);

		if (functionAlias.contains(property)) {
			return String.format("%s %s", property, toJpaDirection(order));
		}

		boolean qualifyReference = !property.contains("("); // ( indicates a function

		for (String joinAlias : joinAliases) {
			if (property.startsWith(joinAlias.concat("."))) {
				qualifyReference = false;
				break;
			}
		}

		String reference = qualifyReference && StringUtils.hasText(alias) ? String.format("%s.%s", alias, property)
				: property;
		String wrapped = order.isIgnoreCase() ? String.format("lower(%s)", reference) : reference;

		return String.format("%s %s", wrapped, toJpaDirection(order));
	}
}

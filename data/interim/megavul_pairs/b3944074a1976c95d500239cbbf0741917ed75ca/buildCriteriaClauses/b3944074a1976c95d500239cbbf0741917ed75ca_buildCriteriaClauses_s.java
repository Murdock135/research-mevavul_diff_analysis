class buildCriteriaClauses {
private List<String> buildCriteriaClauses(String searchTerm, List<String> analysisIds, List<String> timeWindows, List<String> domains) {
		ArrayList<String> clauses = new ArrayList<>();

		if (searchTerm != null && searchTerm.length() > 0) {
			clauses.add(String.format("lower(fr.covariate_name) like '%%%s%%'", searchTerm));
		}

		if (analysisIds != null && analysisIds.size() > 0) {
			ArrayList<Integer> ids = new ArrayList<>();
			ArrayList<String> ranges = new ArrayList<>();

			analysisIds.stream().map((analysisIdExpr) -> analysisIdExpr.split(":"))
							.map(strArray -> Arrays.stream(strArray).map(Integer::parseInt).toArray(Integer[]::new))
							.forEachOrdered((parsedIds) -> {
									if (parsedIds.length > 1) {
										ranges.add(String.format("(ar.analysis_id >= %s and ar.analysis_id <= %s)", parsedIds[0], parsedIds[1]));
									} else {
										ids.add(parsedIds[0]);
									}
							});

			String idClause = "";
			if (ids.size() > 0) {
				idClause = String.format("ar.analysis_id in (%s)", StringUtils.join(ids, ","));
			}

			if (ranges.size() > 0) {
				idClause += (idClause.length() > 0 ? " OR " : "") + StringUtils.join(ranges, " OR ");
			}

			clauses.add("(" + idClause + ")");
		}

		if (timeWindows != null && timeWindows.size() > 0) {
			ArrayList<String> timeWindowClauses = new ArrayList<>();
			timeWindows.forEach((timeWindow) -> {
				timeWindowClauses.add(String.format("ar.analysis_name like '%%%s'", timeWindow));
			});
			clauses.add("(" + StringUtils.join(timeWindowClauses, " OR ") + ")");
		}

		if (domains != null && domains.size() > 0) {
			ArrayList<String> domainClauses = new ArrayList<>();
			domains.forEach((domain) -> {
				if (domain.toLowerCase().equals("null")) {
					domainClauses.add("ar.domain_id is null");
				} else {
					domainClauses.add(String.format("lower(ar.domain_id) = lower('%s')", domain));
				}
			});
			clauses.add("(" + StringUtils.join(domainClauses, " OR ") + ")");
		}

		return clauses;
	}
}

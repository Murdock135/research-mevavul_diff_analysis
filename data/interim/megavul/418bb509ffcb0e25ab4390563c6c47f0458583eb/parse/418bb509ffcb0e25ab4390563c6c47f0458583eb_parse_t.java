class parse {
private void parse(UserRequest ureq) {
		String[] sFiles = ureq.getHttpReq().getParameterValues(FORM_ID);
		if (sFiles == null || sFiles.length == 0) {
			return;
		}
		List<VFSItem> items = currentContainer.getItems();
		if(items != null && !items.isEmpty()) {
			Set<String> itemNames =  items.stream()
					.map(VFSItem::getName)
					.collect(Collectors.toSet());
			for(String sFile:sFiles) {
				if(itemNames.contains(sFile)) {
					files.add(sFile);
				}
			}
		}
	}
}

class renderHead {
@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(JavaScriptHeaderItem.forReference(new MarkdownResourceReference()));
		
		String actionCallback = actionBehavior.getCallbackFunction(explicit("action"), explicit("param1"), explicit("param2"), 
				explicit("param3")).toString();
		String attachmentUploadUrl = attachmentUploadBehavior.getCallbackUrl().toString();
		
		String autosaveKey = getAutosaveKey();
		if (autosaveKey != null)
			autosaveKey = "'" + JavaScriptEscape.escapeJavaScript(autosaveKey) + "'";
		else
			autosaveKey = "undefined";
		
		String script = String.format("onedev.server.markdown.onDomReady('%s', %s, %d, %s, %d, %b, %b, '%s', %s);", 
				container.getMarkupId(), 
				actionCallback, 
				ATWHO_LIMIT, 
				getAttachmentSupport()!=null? "'" + attachmentUploadUrl + "'": "undefined", 
				getAttachmentSupport()!=null? getAttachmentSupport().getAttachmentMaxSize(): 0,
				getUserMentionSupport() != null,
				getReferenceSupport() != null, 
				JavaScriptEscape.escapeJavaScript(ProjectNameValidator.PATTERN.pattern()),
				autosaveKey);
		response.render(OnDomReadyHeaderItem.forScript(script));
		
		script = String.format("onedev.server.markdown.onLoad('%s');", container.getMarkupId());
		response.render(OnLoadHeaderItem.forScript(script));
	}
}

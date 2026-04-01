class onInitialize {
@Override
	protected void onInitialize() {
		super.onInitialize();

		container = new WebMarkupContainer("container");
		container.setOutputMarkupId(true);
		
		add(container);
		
		WebMarkupContainer editLink = new WebMarkupContainer("editLink");
		WebMarkupContainer splitLink = new WebMarkupContainer("splitLink");
		WebMarkupContainer preview = new WebMarkupContainer("preview");
		WebMarkupContainer edit = new WebMarkupContainer("edit");
		container.add(editLink);
		container.add(splitLink);
		container.add(preview);
		container.add(edit);
		
		container.add(AttributeAppender.append("class", compactMode?"compact-mode":"normal-mode"));
		
		container.add(new DropdownLink("doReference") {


			@Override
			protected Component newContent(String id, FloatingPanel dropdown) {
				return new Fragment(id, "referenceMenuFrag", MarkdownEditor.this) {

					@Override
					public void renderHead(IHeaderResponse response) {
						super.renderHead(response);
						String script = String.format("onedev.server.markdown.setupActionMenu($('#%s'), $('#%s'));", 
								container.getMarkupId(), getMarkupId());
						response.render(OnDomReadyHeaderItem.forScript(script));
					}
					
				}.setOutputMarkupId(true);
			}
			
		}.setVisible(getReferenceSupport() != null));
		
		container.add(new DropdownLink("actionMenuTrigger") {


			@Override
			protected Component newContent(String id, FloatingPanel dropdown) {
				return new Fragment(id, "actionMenuFrag", MarkdownEditor.this) {

					@Override
					protected void onInitialize() {
						super.onInitialize();
						add(new WebMarkupContainer("doMention").setVisible(getUserMentionSupport() != null));
						
						if (getReferenceSupport() != null) 
							add(new Fragment("doReference", "referenceMenuFrag", MarkdownEditor.this));
						else 
							add(new WebMarkupContainer("doReference").setVisible(false));
					}

					@Override
					public void renderHead(IHeaderResponse response) {
						super.renderHead(response);
						String script = String.format("onedev.server.markdown.setupActionMenu($('#%s'), $('#%s'));", 
								container.getMarkupId(), getMarkupId());
						response.render(OnDomReadyHeaderItem.forScript(script));
					}
					
				}.setOutputMarkupId(true);
			}
			
		});
		
		container.add(new WebMarkupContainer("doMention").setVisible(getUserMentionSupport() != null));
			
		edit.add(input = new TextArea<String>("input", Model.of(getModelObject())));
		for (AttributeModifier modifier: getInputModifiers()) 
			input.add(modifier);

		if (initialSplit) {
			container.add(AttributeAppender.append("class", "split-mode"));
			preview.add(new Label("rendered", new LoadableDetachableModel<String>() {

				@Override
				protected String load() {
					return renderInput(input.getConvertedInput());
				}
				
			}) {

				@Override
				public void renderHead(IHeaderResponse response) {
					super.renderHead(response);
					String script = String.format(
							"onedev.server.markdown.initRendered($('#%s>.body>.preview>.markdown-rendered'));", 
							container.getMarkupId());
					response.render(OnDomReadyHeaderItem.forScript(script));
				}
				
			}.setEscapeModelStrings(false));
			splitLink.add(AttributeAppender.append("class", "active"));
		} else {
			container.add(AttributeAppender.append("class", "edit-mode"));
			preview.add(new WebMarkupContainer("rendered"));
			editLink.add(AttributeAppender.append("class", "active"));
		}
		
		container.add(new WebMarkupContainer("canAttachFile").setVisible(getAttachmentSupport()!=null));
		
		container.add(actionBehavior = new AbstractPostAjaxBehavior() {

			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
				attributes.setChannel(new AjaxChannel("markdown-preview", AjaxChannel.Type.DROP));
			}

			@Override
			protected void respond(AjaxRequestTarget target) {
				IRequestParameters params = RequestCycle.get().getRequest().getPostParameters();
				String action = params.getParameterValue("action").toString("");
				switch (action) {
				case "render":
					String markdown = params.getParameterValue("param1").toString();
					String rendered = renderInput(markdown);
					String script = String.format("onedev.server.markdown.onRendered('%s', '%s');", 
							container.getMarkupId(), JavaScriptEscape.escapeJavaScript(rendered));
					target.appendJavaScript(script);
					break;
				case "emojiQuery":
					List<String> emojiNames = new ArrayList<>();
					String emojiQuery = params.getParameterValue("param1").toOptionalString();
					if (StringUtils.isNotBlank(emojiQuery)) {
						emojiQuery = emojiQuery.toLowerCase();
						for (String emojiName: EmojiOnes.getInstance().all().keySet()) {
							if (emojiName.toLowerCase().contains(emojiQuery))
								emojiNames.add(emojiName);
						}
						emojiNames.sort((name1, name2) -> name1.length() - name2.length());
					} else {
						emojiNames.add("smile");
						emojiNames.add("worried");
						emojiNames.add("blush");
						emojiNames.add("+1");
						emojiNames.add("-1");
					}

					List<Map<String, String>> emojis = new ArrayList<>();
					for (String emojiName: emojiNames) {
						if (emojis.size() < ATWHO_LIMIT) {
							String emojiCode = EmojiOnes.getInstance().all().get(emojiName);
							CharSequence url = RequestCycle.get().urlFor(new PackageResourceReference(
									EmojiOnes.class, "icon/" + emojiCode + ".png"), new PageParameters());
							Map<String, String> emoji = new HashMap<>();
							emoji.put("name", emojiName);
							emoji.put("url", url.toString());
							emojis.add(emoji);
						}
					}
					String json;
					try {
						json = AppLoader.getInstance(ObjectMapper.class).writeValueAsString(emojis);
					} catch (JsonProcessingException e) {
						throw new RuntimeException(e);
					}
					script = String.format("$('#%s').data('atWhoEmojiRenderCallback')(%s);", container.getMarkupId(), json);
					target.appendJavaScript(script);
					break;
				case "loadEmojis":
					emojis = new ArrayList<>();
					String urlPattern =  RequestCycle.get().urlFor(new PackageResourceReference(EmojiOnes.class,
					        "icon/FILENAME.png"), new PageParameters()).toString();
					
					for (Map.Entry<String, String> entry: EmojiOnes.getInstance().all().entrySet()) {
						Map<String, String> emoji = new HashMap<>();
						emoji.put("name", entry.getKey());
						emoji.put("url", urlPattern.replace("FILENAME", entry.getValue()));
						emojis.add(emoji);
					}

					try {
						json = AppLoader.getInstance(ObjectMapper.class).writeValueAsString(emojis);
					} catch (JsonProcessingException e) {
						throw new RuntimeException(e);
					}

					script = String.format("onedev.server.markdown.onEmojisLoaded('%s', %s);", container.getMarkupId(), json);
					target.appendJavaScript(script);
					break;
				case "userQuery":
					String userQuery = params.getParameterValue("param1").toOptionalString();

					AvatarManager avatarManager = OneDev.getInstance(AvatarManager.class);
					List<Map<String, String>> userList = new ArrayList<>();
					for (User user: getUserMentionSupport().findUsers(userQuery, ATWHO_LIMIT)) {
						Map<String, String> userMap = new HashMap<>();
						userMap.put("name", user.getName());
						if (user.getFullName() != null)
							userMap.put("fullName", user.getFullName());
						String noSpaceName = StringUtils.deleteWhitespace(user.getName());
						if (user.getFullName() != null) {
							String noSpaceFullName = StringUtils.deleteWhitespace(user.getFullName());
							userMap.put("searchKey", noSpaceName + " " + noSpaceFullName);
						} else {
							userMap.put("searchKey", noSpaceName);
						}
						String avatarUrl = avatarManager.getAvatarUrl(user);
						userMap.put("avatarUrl", avatarUrl);
						userList.add(userMap);
					}
					
					try {
						json = OneDev.getInstance(ObjectMapper.class).writeValueAsString(userList);
					} catch (JsonProcessingException e) {
						throw new RuntimeException(e);
					}
					script = String.format("$('#%s').data('atWhoUserRenderCallback')(%s);", container.getMarkupId(), json);
					target.appendJavaScript(script);	
					break;
				case "referenceQuery":
					String referenceQuery = params.getParameterValue("param1").toOptionalString();
					String referenceQueryType = params.getParameterValue("param2").toOptionalString();
					String referenceProjectName = params.getParameterValue("param3").toOptionalString();
					List<Map<String, String>> referenceList = new ArrayList<>();
					Project referenceProject;
					if (StringUtils.isNotBlank(referenceProjectName)) 
						referenceProject = OneDev.getInstance(ProjectManager.class).find(referenceProjectName);
					else
						referenceProject = null;
					if (referenceProject != null || StringUtils.isBlank(referenceProjectName)) {
						if ("issue".equals(referenceQueryType)) {
							for (Issue issue: getReferenceSupport().findIssues(referenceProject, referenceQuery, ATWHO_LIMIT)) {
								Map<String, String> referenceMap = new HashMap<>();
								referenceMap.put("referenceType", "issue");
								referenceMap.put("referenceNumber", String.valueOf(issue.getNumber()));
								referenceMap.put("referenceTitle", issue.getTitle());
								referenceMap.put("searchKey", issue.getNumber() + " " + StringUtils.deleteWhitespace(issue.getTitle()));
								referenceList.add(referenceMap);
							}
						} else if ("pullrequest".equals(referenceQueryType)) {
							for (PullRequest request: getReferenceSupport().findPullRequests(referenceProject, referenceQuery, ATWHO_LIMIT)) {
								Map<String, String> referenceMap = new HashMap<>();
								referenceMap.put("referenceType", "pull request");
								referenceMap.put("referenceNumber", String.valueOf(request.getNumber()));
								referenceMap.put("referenceTitle", request.getTitle());
								referenceMap.put("searchKey", request.getNumber() + " " + StringUtils.deleteWhitespace(request.getTitle()));
								referenceList.add(referenceMap);
							}
						} else if ("build".equals(referenceQueryType)) {
							for (Build build: getReferenceSupport().findBuilds(referenceProject, referenceQuery, ATWHO_LIMIT)) {
								Map<String, String> referenceMap = new HashMap<>();
								referenceMap.put("referenceType", "build");
								referenceMap.put("referenceNumber", String.valueOf(build.getNumber()));
								
								String title;
								if (build.getVersion() != null) 
									title = "(" + build.getVersion() + ") " + build.getJobName();
								else
									title = build.getJobName();
								referenceMap.put("referenceTitle", title);
								referenceMap.put("searchKey", build.getNumber() + " " + StringUtils.deleteWhitespace(title));
								referenceList.add(referenceMap);
							}
						}
					}
					
					try {
						json = OneDev.getInstance(ObjectMapper.class).writeValueAsString(referenceList);
					} catch (JsonProcessingException e) {
						throw new RuntimeException(e);
					}
					script = String.format("$('#%s').data('atWhoReferenceRenderCallback')(%s);", container.getMarkupId(), json);
					target.appendJavaScript(script);
					break;
				case "selectImage":
				case "selectLink":
					new ModalPanel(target) {
						
						@Override
						protected Component newContent(String id) {
							return new InsertUrlPanel(id, MarkdownEditor.this, action.equals("selectImage")) {

								@Override
								protected void onClose(AjaxRequestTarget target) {
									close();
								}
								
							};
						}

						@Override
						protected void onClosed() {
							super.onClosed();
							AjaxRequestTarget target = 
									Preconditions.checkNotNull(RequestCycle.get().find(AjaxRequestTarget.class));
							target.appendJavaScript(String.format("$('#%s textarea').focus();", container.getMarkupId()));
						}
						
					};
					break;
				case "insertUrl":
					String name;
					try {
						name = URLDecoder.decode(params.getParameterValue("param1").toString(), StandardCharsets.UTF_8.name());
					} catch (UnsupportedEncodingException e) {
						throw new RuntimeException(e);
					}
					String replaceMessage = params.getParameterValue("param2").toString();
					String url = getAttachmentSupport().getAttachmentUrl(name);
					insertUrl(target, isWebSafeImage(name), url, name, replaceMessage);
					
					break;
				default:
					throw new IllegalStateException("Unknown action: " + action);
				}		
			}
			
		});
		
		container.add(attachmentUploadBehavior = new AbstractPostAjaxBehavior() {
			
			@Override
			protected void respond(AjaxRequestTarget target) {
				Preconditions.checkNotNull(getAttachmentSupport(), "Unexpected attachment upload request");
				HttpServletRequest request = (HttpServletRequest) RequestCycle.get().getRequest().getContainerRequest();
				HttpServletResponse response = (HttpServletResponse) RequestCycle.get().getResponse().getContainerResponse();
				try {
					String fileName = FilenameUtils.sanitizeFilename(
							URLDecoder.decode(request.getHeader("File-Name"), StandardCharsets.UTF_8.name()));
					String attachmentName = getAttachmentSupport().saveAttachment(fileName, request.getInputStream());
					response.getWriter().print(URLEncoder.encode(attachmentName, StandardCharsets.UTF_8.name()));
					response.setStatus(HttpServletResponse.SC_OK);
				} catch (Exception e) {
					logger.error("Error uploading attachment.", e);
					try {
						if (e.getMessage() != null)
							response.getWriter().print(e.getMessage());
						else
							response.getWriter().print("Internal server error");
					} catch (IOException e2) {
						throw new RuntimeException(e2);
					}
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}
			}
			
		});
	}
}

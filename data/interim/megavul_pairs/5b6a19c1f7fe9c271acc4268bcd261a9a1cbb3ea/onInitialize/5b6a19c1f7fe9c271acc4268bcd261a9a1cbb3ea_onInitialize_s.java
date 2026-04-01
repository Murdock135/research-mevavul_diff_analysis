class onInitialize {
@Override
	protected void onInitialize() {
		super.onInitialize();
		
		int maxUploadFileSize = OneDev.getInstance(SettingManager.class)
				.getPerformanceSetting().getMaxUploadFileSize();
		
		Form<?> form = new Form<Void>("form");
		form.setMultiPart(true);
		form.setFileMaxSize(Bytes.megabytes(maxUploadFileSize));
		add(form);
		
		form.add(new AjaxLink<Void>("close") {

			@Override
			public void onClick(AjaxRequestTarget target) {
				onCancel(target);
			}
			
		});
		
		FencedFeedbackPanel feedback = new FencedFeedbackPanel("feedback", form);
		feedback.setOutputMarkupPlaceholderTag(true);
		form.add(feedback);
		
		DropzoneField dropzone = new DropzoneField(
				"files", 
				new PropertyModel<Collection<FileUpload>>(this, "uploads"), 
				null, 0, maxUploadFileSize);
		dropzone.setRequired(true).setLabel(Model.of("File"));
		form.add(dropzone);
		
		form.add(new AjaxButton("upload") {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				super.onSubmit(target, form);
				
				LockUtils.write(getBuild().getArtifactsLockKey(), new Callable<Void>() {

					@Override
					public Void call() throws Exception {
						File artifactsDir = getBuild().getArtifactsDir();
						for (FileUpload upload: uploads) {
							String filePath = FilenameUtils.sanitizeFilename(upload.getFileName());
							if (directory != null)
								filePath = directory + "/" + filePath;
							File file = new File(artifactsDir, filePath);
							FileUtils.createDir(file.getParentFile());
							try (	InputStream is = upload.getInputStream();
									OutputStream os = new FileOutputStream(file)) {
								IOUtils.copy(is, os);
							} finally {
								upload.release();
							}
						}
						return null;
					}
					
				});
				
				onUploaded(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				super.onError(target, form);
				target.add(feedback);
			}
			
		});
		
		form.add(new TextField<String>("directory", new PropertyModel<String>(this, "directory")));
		
		form.add(new AjaxLink<Void>("cancel") {

			@Override
			public void onClick(AjaxRequestTarget target) {
				onCancel(target);
			}
			
		});
	}
}

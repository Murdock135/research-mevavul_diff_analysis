class resolve {
@Override
	public @Nullable String resolve(@NotNull Dragonfly dragonfly, @NotNull MavenDependency dependency) throws ResolveFailureException {
		if (!dependency.getVersion().contains("SNAPSHOT")) {
			throw new ResolveFailureException("Cannot resolve a dependency as a snapshot if it isn't a snapshot");
		} else {
			Set<String> urls = getUrls(dragonfly, dependency);
			if (urls.isEmpty()) {
				throw new ResolveFailureException("Cannot resolve dependency: " + dependency);
			}

			String data = null;
			String resolvedUrl = null;
			for (String url : urls) {
				if ((data = get(url, dragonfly.getTimeout())) != null) {
					resolvedUrl = url;
					break;
				}
			}

			if (null == data) {
				throw new ResolveFailureException("Cannot resolve dependency: " + dependency);
			}

			try {
				DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
				Document document = builder.parse(new InputSource(new StringReader(data)));
				Element root = document.getDocumentElement();
				Element snapshotData = (Element) root.getElementsByTagName("snapshot").item(0);

				String timestamp = snapshotData.getElementsByTagName("timestamp").item(0).getTextContent();
				String buildNumber = snapshotData.getElementsByTagName("buildNumber").item(0).getTextContent();

				return String.format(
						OUTPUT_FORMAT,
						resolvedUrl.replace("/maven-metadata.xml", ""),
						dependency.getArtifactId(),
						dependency.getVersion().replace("-SNAPSHOT", ""),
						timestamp,
						buildNumber
				);
			} catch (Exception ex) {
				throw new ResolveFailureException("Cannot resolve dependency: " + dependency, ex);
			}
		}
	}
}

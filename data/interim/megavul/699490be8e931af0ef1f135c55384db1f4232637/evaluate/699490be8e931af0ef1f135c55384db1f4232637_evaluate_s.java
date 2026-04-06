class evaluate {
public static ResourceEvaluation evaluate(File file, String filename) {
		ResourceEvaluation eval = new ResourceEvaluation();
		try {
			ImsManifestFileFilter visitor = new ImsManifestFileFilter();
			Path fPath = PathUtils.visit(file, filename, visitor);
			if(visitor.hasManifest()) {
				Path realManifestPath = visitor.getManifestPath();
				Path manifestPath = fPath.resolve(realManifestPath);
				
				RootSearcher rootSearcher = new RootSearcher();
				Files.walkFileTree(fPath, rootSearcher);
				if(rootSearcher.foundRoot()) {
					manifestPath = rootSearcher.getRoot().resolve(IMS_MANIFEST);
				} else {
					manifestPath = fPath.resolve(IMS_MANIFEST);
				}

				Document doc = IMSLoader.loadIMSDocument(manifestPath);
				if(validateImsManifest(doc)) {
					if(visitor.hasEditorTreeModel()) {
						XMLScanner scanner = new XMLScanner();
						scanner.scan(visitor.getEditorTreeModelPath());
						eval.setValid(!scanner.hasEditorTreeModelMarkup());	
					} else {
						eval.setValid(true);
					}
				} else {
					eval.setValid(false);
				}
			} else {
				eval.setValid(false);
			}
			PathUtils.closeSubsequentFS(fPath);
		} catch (IOException | IllegalArgumentException e) {
			log.error("", e);
			eval.setValid(false);
		}
		return eval;
	}
}

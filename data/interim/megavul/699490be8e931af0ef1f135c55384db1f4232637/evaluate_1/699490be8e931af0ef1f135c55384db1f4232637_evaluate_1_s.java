class evaluate_1 {
public static ResourceEvaluation evaluate(File file, String filename) {
		ResourceEvaluation eval = new ResourceEvaluation();
		try {
			ImsManifestFileFilter visitor = new ImsManifestFileFilter();
			Path fPath = PathUtils.visit(file, filename, visitor);
			if(visitor.isValid()) {
				Path realManifestPath = visitor.getManifestPath();
				Path manifestPath = fPath.resolve(realManifestPath);
				
				RootSearcher rootSearcher = new RootSearcher();
				Files.walkFileTree(fPath, rootSearcher);
				if(rootSearcher.foundRoot()) {
					manifestPath = rootSearcher.getRoot().resolve(IMS_MANIFEST);
				} else {
					manifestPath = fPath.resolve(IMS_MANIFEST);
				}
				
				QTI21ContentPackage	cp = new QTI21ContentPackage(manifestPath);
				if(validateImsManifest(cp, new PathResourceLocator(manifestPath.getParent()))) {
					eval.setValid(true);
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

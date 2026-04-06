class load_2 {
public CompletableFuture<Boolean> load(Dependency... dependencies) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				statusHandler.accept(Status.STARTING);
				List<Dependency> dependencyList = Arrays.stream(dependencies)
						.sorted(Comparator.comparingInt(Dependency::getPriority)).collect(Collectors.toList());

				if (dependencyList.size() > 0) {
					List<Dependency> downloadList = dependencyList.stream().filter(d -> !isDownloaded(d))
							.collect(Collectors.toList());
					if (downloadList.size() > 0) {
						statusHandler.accept(Status.DOWNLOADING);
						dependencyDownloader.download(downloadList);

						if (downloadList.stream().anyMatch(d -> d.getRelocations().size() > 0)) {
							statusHandler.accept(Status.RELOCATING);
							dependencyRelocator.relocate(downloadList);
						}
					}

					dependencyList.stream().filter(d -> d.getRelocations().size() > 0 && !d.isRelocated())
							.forEach(d -> d.setFileName(dependencyRelocator.getRelocatedFileName(d)));

					statusHandler.accept(Status.LOADING);
					dependencyLoader.load(dependencyList);
				}

				statusHandler.accept(Status.FINISHED);
				return true;
			} catch (Exception ex) {
				throw new IllegalStateException(ex);
			}
		});
	}
}

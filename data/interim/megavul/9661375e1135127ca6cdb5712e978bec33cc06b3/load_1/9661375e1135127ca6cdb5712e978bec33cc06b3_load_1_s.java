class load_1 {
public void load(List<Dependency> dependencies) throws LoadFailureException {
		for (Dependency dependency : dependencies) {
			load(dependency);
		}
	}
}

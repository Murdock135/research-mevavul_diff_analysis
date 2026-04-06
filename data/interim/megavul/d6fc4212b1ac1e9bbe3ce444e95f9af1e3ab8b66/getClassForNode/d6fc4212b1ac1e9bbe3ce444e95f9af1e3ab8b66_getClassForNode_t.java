class getClassForNode {
@Override
		protected Class<?> getClassForNode(Node node) {
			if (node instanceof VersionedYamlDoc) {
				return super.getClassForNode(node);
			} else {
				Class<?> type = node.getType();
				if (type.getAnnotation(Editable.class) == null) {
					// Do not deserialize unknown classes to avoid security vulnerabilities
					throw new IllegalStateException(String.format("Unexpected yaml node (type: %s, tag: %s)", 
							type, node.getTag()));
				} else {
					if (!ClassUtils.isConcrete(type)) {
						ImplementationRegistry registry = OneDev.getInstance(ImplementationRegistry.class);
						for (Class<?> implementationClass: registry.getImplementations(node.getType())) {
							String implementationTag = new Tag("!" + implementationClass.getSimpleName()).getValue();
							if (implementationTag.equals(node.getTag().getValue()))
								return implementationClass;
						}
					}
					return super.getClassForNode(node);
				}
			}
		}
}

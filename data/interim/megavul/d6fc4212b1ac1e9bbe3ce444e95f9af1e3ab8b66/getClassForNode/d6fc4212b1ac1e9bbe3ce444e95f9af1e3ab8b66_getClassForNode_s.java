class getClassForNode {
@Override
		protected Class<?> getClassForNode(Node node) {
			Class<?> type = node.getType();
			if (type.getAnnotation(Editable.class) != null && !ClassUtils.isConcrete(type)) {
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

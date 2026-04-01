class equals {
@Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ClassPathResource) {
            ClassPathResource otherRes = (ClassPathResource) obj;

            ClassLoader thisLoader = this.classLoader;
            ClassLoader otherLoader = otherRes.classLoader;

            return (this.path.equals(otherRes.path) &&
                    thisLoader.equals(otherLoader) &&
                    this.clazz.equals(otherRes.clazz));
        }
        return false;
    }
}

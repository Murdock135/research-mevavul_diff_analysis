class resolveDriverClassName {
public String resolveDriverClassName(DriverClassNameResolveRequest request) {
        return driverResources.resolveSqlDriverNameFromJar(request.getJdbcDriverFileUrl());
    }
}

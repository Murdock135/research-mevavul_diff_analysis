class resolveDriverClassName {
public String resolveDriverClassName(DriverClassNameResolveRequest request) {
        return driverResources.resolveDriverClassName(request.getJdbcDriverFileUrl());
    }
}

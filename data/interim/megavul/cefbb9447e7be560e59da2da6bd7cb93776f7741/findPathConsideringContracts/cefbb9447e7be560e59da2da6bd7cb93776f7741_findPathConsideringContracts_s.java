class findPathConsideringContracts {
private URL findPathConsideringContracts(ClassLoader loader, 
                                     LibraryInfo library,
                                     String resourceName,
                                     String localePrefix,
                                     ContractInfo [] outContract,
                                     String [] outBasePath,
                                     FacesContext ctx) {
        UIViewRoot root = ctx.getViewRoot();
        List<String> contracts = null;
        URL result = null;
        
        if (library != null) {
            if(library.getContract() == null) {
                contracts = Collections.emptyList();
            } else {
                contracts = new ArrayList<String>(1);
                contracts.add(library.getContract());
            }
        } else if (root == null) {
            String contractName = ctx.getExternalContext().getRequestParameterMap()
                  .get("con");
            if (null != contractName && 0 < contractName.length()) {
                contracts = new ArrayList<>();
                contracts.add(contractName);
            } else {
                return null;
            }
        } else {
       		contracts = ctx.getResourceLibraryContracts();
        }

        String basePath = null;
        
        for (String curContract : contracts) {
        
            if (library != null) {
                // PENDING(fcaputo) no need to iterate over the contracts, if we have a library
                basePath = library.getPath(localePrefix) + '/' + resourceName;
            } else {
                if (localePrefix == null) {
                    basePath = getBaseContractsPath() + '/' + curContract + '/' + resourceName;
                } else {
                    basePath = getBaseContractsPath()
                            + '/' + curContract 
                            + '/'
                            + localePrefix
                            + '/'
                            + resourceName;
                }
            }
            
            if (null != (result = loader.getResource(basePath))) {
                outContract[0] = new ContractInfo(curContract);
                outBasePath[0] = basePath;
                break;
            } else {
                basePath = null;
            }
        }
            
        return result;
    }
}

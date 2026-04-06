class findPathConsideringContracts_1 {
private String findPathConsideringContracts(LibraryInfo library,
                                     String resourceName,
                                     String localePrefix,
                                     ContractInfo [] outContract,
                                     FacesContext ctx) {
        UIViewRoot root = ctx.getViewRoot();
        List<String> contracts = null;

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
            if (null != contractName && 0 < contractName.length() && !ResourceManager.nameContainsForbiddenSequence(contractName)) {
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
            
            try {
                if (ctx.getExternalContext().getResource(basePath) != null) {
                    outContract[0] = new ContractInfo(curContract);
                    break;
                } else {
                    basePath = null;
                }
            } catch (MalformedURLException e) {
                throw new FacesException(e);
            }
        }
            
        return basePath;
    }
}

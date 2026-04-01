class submoduleAdd {
@TestOnly
    public void submoduleAdd(String repoUrl, String submoduleNameToPutInGitSubmodules, String folder) {
        String[] addSubmoduleWithSameNameArgs = new String[]{"submodule", "add", "--", repoUrl, folder};
        String[] changeSubmoduleNameInGitModules = new String[]{"config", "--file", ".gitmodules", "--rename-section", "submodule." + folder, "submodule." + submoduleNameToPutInGitSubmodules};
        String[] addGitModules = new String[]{"add", ".gitmodules"};

        runOrBomb(gitWd().withArgs(addSubmoduleWithSameNameArgs));
        runOrBomb(gitWd().withArgs(changeSubmoduleNameInGitModules));
        runOrBomb(gitWd().withArgs(addGitModules));
    }
}

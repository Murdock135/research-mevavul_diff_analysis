class run {
public int run(String[] args) throws IOException, RemoteException {
        boolean validCommand = false;
        if (args.length < 1) {
            return showUsage();
        }

        mUm = IUserManager.Stub.asInterface(ServiceManager.getService("user"));
        mPm = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
        if (mPm == null) {
            System.err.println(PM_NOT_RUNNING_ERR);
            return 1;
        }
        mInstaller = mPm.getPackageInstaller();

        mArgs = args;
        String op = args[0];
        mNextArg = 1;

        if ("list".equals(op)) {
            return runList();
        }

        if ("path".equals(op)) {
            return runPath();
        }

        if ("dump".equals(op)) {
            return runDump();
        }

        if ("install".equals(op)) {
            return runInstall();
        }

        if ("install-create".equals(op)) {
            return runInstallCreate();
        }

        if ("install-write".equals(op)) {
            return runInstallWrite();
        }

        if ("install-commit".equals(op)) {
            return runInstallCommit();
        }

        if ("install-abandon".equals(op) || "install-destroy".equals(op)) {
            return runInstallAbandon();
        }

        if ("set-installer".equals(op)) {
            return runSetInstaller();
        }

        if ("uninstall".equals(op)) {
            return runUninstall();
        }

        if ("clear".equals(op)) {
            return runClear();
        }

        if ("enable".equals(op)) {
            return runSetEnabledSetting(PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
        }

        if ("disable".equals(op)) {
            return runSetEnabledSetting(PackageManager.COMPONENT_ENABLED_STATE_DISABLED);
        }

        if ("disable-user".equals(op)) {
            return runSetEnabledSetting(PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER);
        }

        if ("disable-until-used".equals(op)) {
            return runSetEnabledSetting(PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED);
        }

        if ("hide".equals(op)) {
            return runSetHiddenSetting(true);
        }

        if ("unhide".equals(op)) {
            return runSetHiddenSetting(false);
        }

        if ("grant".equals(op)) {
            return runGrantRevokePermission(true);
        }

        if ("revoke".equals(op)) {
            return runGrantRevokePermission(false);
        }

        if ("reset-permissions".equals(op)) {
            return runResetPermissions();
        }

        if ("set-permission-enforced".equals(op)) {
            return runSetPermissionEnforced();
        }

        if ("set-app-link".equals(op)) {
            return runSetAppLink();
        }

        if ("get-app-link".equals(op)) {
            return runGetAppLink();
        }

        if ("set-install-location".equals(op)) {
            return runSetInstallLocation();
        }

        if ("get-install-location".equals(op)) {
            return runGetInstallLocation();
        }

        if ("trim-caches".equals(op)) {
            return runTrimCaches();
        }

        if ("create-user".equals(op)) {
            return runCreateUser();
        }

        if ("remove-user".equals(op)) {
            return runRemoveUser();
        }

        if ("get-max-users".equals(op)) {
            return runGetMaxUsers();
        }

        if ("force-dex-opt".equals(op)) {
            return runForceDexOpt();
        }

        if ("move-package".equals(op)) {
            return runMovePackage();
        }

        if ("move-primary-storage".equals(op)) {
            return runMovePrimaryStorage();
        }

        try {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("-l")) {
                    validCommand = true;
                    return runListPackages(false);
                } else if (args[0].equalsIgnoreCase("-lf")){
                    validCommand = true;
                    return runListPackages(true);
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("-p")) {
                    validCommand = true;
                    return displayPackageFilePath(args[1]);
                }
            }
            return 1;
        } finally {
            if (validCommand == false) {
                if (op != null) {
                    System.err.println("Error: unknown command '" + op + "'");
                }
                showUsage();
            }
        }
    }
}

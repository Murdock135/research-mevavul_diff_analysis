class getDestination {
@Override
  public String getDestination(String function, JobDomainPeasSessionController jobDomainSC,
      HttpRequest request) {
    String destination = "";


    try {
      if (!jobDomainSC.isAccessGranted()) {
        throw new JobDomainPeasException("Bad right for user {0}", jobDomainSC.getUserId());
      }
      // 1) Performs the action
      // ----------------------
      if (function.startsWith("selectUserOrGroup")) {
        String id;

        function = DOMAIN_CONTENT_FCT;
        id = jobDomainSC.getSelectedUserId();
        if (id != null) {
          jobDomainSC.setTargetUser(id);
          function = USER_CONTENT_FCT;
        } else {
          id = jobDomainSC.getSelectedGroupId();
          if (id != null) {
            jobDomainSC.goIntoGroup(id);
            function = GROUP_CONTENT_FCT;
          }
        }
      }

      if ("blankUsers".equals(function)) {
        final List<String> userIds = new ArrayList<>();
        request.mergeSelectedItemsInto(userIds);
        if (!userIds.isEmpty()) {
          jobDomainSC.blankDeletedUsers(userIds);
        }
        function = DOMAIN_CONTENT_FCT;
      }

      if (function.startsWith("Main")) {
        jobDomainSC.returnIntoGroup(null);
        jobDomainSC.setDefaultTargetDomain();
        destination = "jobDomain.jsp";
      } else if ("PreviousUser".equals(function)) {
        UserDetail user = jobDomainSC.getPrevious();
        jobDomainSC.setTargetUser(user.getId());
        destination = USER_CONTENT_DEST;
      } else if ("NextUser".equals(function)) {
        UserDetail user = jobDomainSC.getNext();
        jobDomainSC.setTargetUser(user.getId());
        destination = USER_CONTENT_DEST;
      } else if ("restoreUsers".equals(function)) {
        final List<String> userIds = new ArrayList<>();
        request.mergeSelectedItemsInto(userIds);
        for (final String u : userIds) {
          jobDomainSC.restoreUser(u);
        }
        destination = getDestination(DISPLAY_REMOVED_USERS_DEST, jobDomainSC, request);
      } else if ("deleteUsers".equals(function)) {
        final List<String> userIds = new ArrayList<>();
        request.mergeSelectedItemsInto(userIds);
        for (final String u : userIds) {
          jobDomainSC.deleteUser(u);
        }
        destination = getDestination(DISPLAY_REMOVED_USERS_DEST, jobDomainSC, request);
      } else if ("restoreGroups".equals(function)) {
        final List<String> groupIds = new ArrayList<>();
        request.mergeSelectedItemsInto(groupIds);
        boolean refreshDomainNav = false;
        for (final String group : groupIds) {
          refreshDomainNav |= jobDomainSC.restoreGroup(group);
        }
        if (refreshDomainNav) {
          reloadDomainNavigation(request);
        }
        destination = getDestination(DISPLAY_REMOVED_GROUPS_DEST, jobDomainSC, request);
      } else if ("deleteGroups".equals(function)) {
        final List<String> groupIds = new ArrayList<>();
        request.mergeSelectedItemsInto(groupIds);
        for (final String group : groupIds) {
          jobDomainSC.deleteGroup(group);
        }
        destination = getDestination(DISPLAY_REMOVED_GROUPS_DEST, jobDomainSC, request);
      } else if (function.startsWith("user")) {
        // USER Actions --------------------------------------------
        String userId = request.getParameter("Iduser");
        if (function.startsWith(USER_CONTENT_FCT)) {
          if (isDefined(userId)) {
            jobDomainSC.setTargetUser(userId);
          }
        } else if ("userGetP12".equals(function)) {
          jobDomainSC.getP12(userId);
        } else if (function.startsWith("userCreate")) {
          UserRequestData userRequestData =
              RequestParameterDecoder.decode(request, UserRequestData.class);

          // process extra properties
          HashMap<String, String> properties = getExtraPropertyValues(request);

          jobDomainSC.createUser(userRequestData, properties, request);

        } else if (function.startsWith("usersCsvImport")) {
          List<FileItem> fileItems = request.getFileItems();
          UserRequestData userRequestData =
              RequestParameterDecoder.decode(request, UserRequestData.class);

          FileItem fileItem = FileUploadUtil.getFile(fileItems, "file_upload");

          if (fileItem != null) {
            jobDomainSC.importCsvUsers(fileItem, userRequestData, request);
          }

          destination = DOMAIN_CONTENT_DEST;
        } else if (function.startsWith("userUpdate")) {
          UserRequestData userRequestData =
              RequestParameterDecoder.decode(request, UserRequestData.class);

          // process extra properties
          HashMap<String, String> properties = getExtraPropertyValues(request);

          jobDomainSC.modifyUser(userRequestData, properties, request);
        } else if (function.startsWith("userBlock")) {
          jobDomainSC.blockUser(userId);
        } else if (function.startsWith("userUnblock")) {
          jobDomainSC.unblockUser(userId);
        } else if (function.startsWith("userDeactivate")) {
          jobDomainSC.deactivateUser(userId);
        } else if (function.startsWith("userActivate")) {
          jobDomainSC.activateUser(userId);
        } else if (function.startsWith("userDelete")) {
          jobDomainSC.deleteUser(userId);
        } else if (function.startsWith("userRemove")) {
          jobDomainSC.removeUser(userId);
        } else if (function.startsWith("userAvatarDelete")) {
          jobDomainSC.deleteUserAvatar(userId);
        } else if ("userViewRights".equals(function)) {
          request.setAttribute("UserProfiles", jobDomainSC.getCurrentProfiles());
        } else if (function.startsWith("userMS")) {
          UserRequestData userRequestData =
              RequestParameterDecoder.decode(request, UserRequestData.class);

          // process extra properties
          HashMap<String, String> properties = getExtraPropertyValues(request);

          jobDomainSC.modifySynchronizedUser(userRequestData, properties, request);
        } else if (function.startsWith("userSearchToImport")) {
          Map<String, String> query;
          List<UserDetail> users;
          jobDomainSC.clearListSelectedUsers();
          jobDomainSC.setIndexOfFirstItemToDisplay("0");

          String fromArray = request.getParameter("FromArray");
          if (isDefined(fromArray)) {
            query = jobDomainSC.getQueryToImport();
            users = jobDomainSC.getUsersToImport();
          } else {
            query = new Hashtable<>();
            Enumeration<String> parameters = request.getParameterNames();
            String paramName;
            String paramValue;
            while (parameters.hasMoreElements()) {
              paramName = parameters.nextElement();
              if (!paramName.startsWith("Pagination") && !paramName.equals(SESSION_TOKEN_KEY)) {
                paramValue = request.getParameter(paramName);
                if (isDefined(paramValue)) {
                  query.put(paramName, paramValue);
                }
              }
            }

            users = jobDomainSC.searchUsers(query);
          }

          request.setAttribute("Query", query);
          request.setAttribute(USERS_ATTR, users);

          destination = getDestination(DISPLAY_USER_IMPORT_FCT, jobDomainSC, request);
        } else if ("userImport".equals(function)) {
          String[] specificIds = request.getParameterValues("specificIds");
          // Massive users import
          if (specificIds != null) {
            processSelection(request, jobDomainSC);
            specificIds = new String[jobDomainSC.getListSelectedUsers().size()];
            jobDomainSC.getListSelectedUsers().toArray(specificIds);
            jobDomainSC.importUsers(specificIds);
          } else {
            // Unitary user Import
            String specificId = request.getParameter("specificIds");
            if (isDefined(specificId)) {
              jobDomainSC.importUser(specificId);
            }
          }
        } else if ("userImportAll".equals(function)) {
          Iterator<UserDetail> usersIt = jobDomainSC.getUsersToImport().iterator();
          ArrayList<String> listSelectedUsersIds = new ArrayList<>();
          while (usersIt.hasNext()) {
            listSelectedUsersIds.add(usersIt.next().getSpecificId());
          }
          jobDomainSC.setListSelectedUsers(listSelectedUsersIds);
          String[] specificIds = new String[jobDomainSC.getListSelectedUsers().size()];
          jobDomainSC.getListSelectedUsers().toArray(specificIds);
          jobDomainSC.importUsers(specificIds);
        } else if ("userView".equals(function)) {
          String specificId = request.getParameter("specificId");

          UserFull user = jobDomainSC.getUser(specificId);

          request.setAttribute("UserFull", user);

          destination = "userView.jsp";
        } else if (function.startsWith("userSynchro")) {
          jobDomainSC.synchroUser(userId);
        } else if (function.startsWith("userUnSynchro")) {
          jobDomainSC.unsynchroUser(userId);
        } else if ("userOpen".equals(function)) {
          userId = request.getParameter("userId");

          UserDetail user = UserDetail.getById(userId);
          String domainId = user.getDomainId();
          if (domainId == null) {
            domainId = Domain.MIXED_DOMAIN_ID;
          }

          // not refresh the domain
          jobDomainSC.setRefreshDomain(false);

          // domaine
          jobDomainSC.setTargetDomain(domainId);

          // réinitialise les groupes
          jobDomainSC.returnIntoGroup(null);

          // groupe d'appartenance
          AdminController adminController = ServiceProvider.getService(AdminController.class);
          List<GroupDetail> groups = adminController.getDirectGroupsOfUser(userId);
          for (final Group group : groups) {
            String groupDomainId = group.getDomainId();
            if (groupDomainId == null) {
              groupDomainId = "-1";
            }
            if (!"-1".equals(groupDomainId)) {
              jobDomainSC.goIntoGroup(group.getId());
              break;
            }
          }

          // user
          jobDomainSC.setTargetUser(userId);
        }
        if (destination.length() <= 0) {
          if (jobDomainSC.getTargetUserDetail() != null) {
            destination = USER_CONTENT_DEST;
          } else {
            destination = getDestination(GROUP_CONTENT_FCT, jobDomainSC, request);
          }
        }
      } else if (function.startsWith("group")) {
        // GROUP Actions --------------------------------------------
        boolean bHaveToRefreshDomain = false;

        jobDomainSC.setTargetUser(null);

        // Browse functions
        // ----------------
        if (function.startsWith(GROUP_CONTENT_FCT)) {
          String groupId = request.getParameter(IDGROUP_PARAM);
          if (isDefined(groupId)) {
            jobDomainSC.goIntoGroup(groupId);
          }
        } else if (function.startsWith("groupExport.txt")) {
          String groupId = request.getParameter(IDGROUP_PARAM);
          if (isDefined(groupId)) {
            jobDomainSC.goIntoGroup(request.getParameter(IDGROUP_PARAM));
            destination = "exportgroup.jsp";
          }
        } else if (function.startsWith("groupReturn")) {
          jobDomainSC.returnIntoGroup(request.getParameter(IDGROUP_PARAM));
        } else if (function.startsWith("groupSet")) {
          jobDomainSC.returnIntoGroup(null);
          jobDomainSC.goIntoGroup(request.getParameter(IDGROUP_PARAM));
        } else if (function.startsWith("groupCreate")) {
          bHaveToRefreshDomain = jobDomainSC.createGroup(request.getParameter("Idparent"),
              WebEncodeHelper.htmlStringToJavaString(request.getParameter(GROUP_NAME_PARAM)),
              WebEncodeHelper.htmlStringToJavaString(request.getParameter("groupDescription")),
              request.getParameter("groupRule"));
        } else if (function.startsWith("groupUpdate")) {
          bHaveToRefreshDomain = jobDomainSC.modifyGroup(request.getParameter(IDGROUP_PARAM),
              WebEncodeHelper.htmlStringToJavaString(request.getParameter(GROUP_NAME_PARAM)),
              WebEncodeHelper.htmlStringToJavaString(request.getParameter("groupDescription")),
              request.getParameter("groupRule"));
        } else if (function.startsWith("groupAddRemoveUsers")) {
          bHaveToRefreshDomain = jobDomainSC
              .updateGroupSubUsers(jobDomainSC.getTargetGroup().getId(), jobDomainSC.getSelectedUsersIds());
        } else if (function.startsWith("groupRemove")) {
          bHaveToRefreshDomain = jobDomainSC.removeGroup(request.getParameter(IDGROUP_PARAM));
        } else if (function.startsWith("groupDelete")) {
          bHaveToRefreshDomain = jobDomainSC.deleteGroup(request.getParameter(IDGROUP_PARAM));
        } else if (function.startsWith("groupSynchro")) {
          final Optional<Group> synchronizedGroup = jobDomainSC.synchroGroup(request.getParameter(IDGROUP_PARAM));
          if (synchronizedGroup.isPresent()) {
            final Group group = synchronizedGroup.get();
            if (group.isRemovedState()) {
              reloadDomainNavigation(request);
            } else {
              bHaveToRefreshDomain = true;
            }
          }
        } else if (function.startsWith("groupUnSynchro")) {
          bHaveToRefreshDomain = jobDomainSC.unsynchroGroup(request.getParameter(IDGROUP_PARAM));
        } else if (function.startsWith("groupImport")) {
          bHaveToRefreshDomain = jobDomainSC.importGroup(WebEncodeHelper.htmlStringToJavaString(request.getParameter(
              GROUP_NAME_PARAM)));
        } else if ("groupManagersView".equals(function)) {
          List<List> groupManagers = jobDomainSC.getGroupManagers();

          request.setAttribute(USERS_ATTR, groupManagers.get(0));
          request.setAttribute("Groups", groupManagers.get(1));

          destination = "groupManagers.jsp";
        } else if ("groupManagersChoose".equals(function)) {
          List<String> userIds = (List<String>) StringUtil
              .splitString(request.getParameter("UserPanelCurrentUserIds"), ',');
          List<String> groupIds = (List<String>) StringUtil
              .splitString(request.getParameter("UserPanelCurrentGroupIds"), ',');
          jobDomainSC.initUserPanelForGroupManagers(userIds, groupIds);
          destination = Selection.getSelectionURL();
        } else if ("groupManagersUpdate".equals(function)) {
          List<String> userIds = (List<String>) StringUtil
              .splitString(request.getParameter("roleItems" + "UserPanelCurrentUserIds"), ',');
          List<String> groupIds = (List<String>) StringUtil
              .splitString(request.getParameter("roleItems" + "UserPanelCurrentGroupIds"), ',');
          jobDomainSC.updateGroupProfile(userIds, groupIds);

          destination = getDestination("groupManagersView", jobDomainSC, request);
        } else if ("groupOpen".equals(function)) {
          String groupId = request.getParameter("groupId");

          if (jobDomainSC.isAccessGranted() || jobDomainSC.isGroupManagerOnGroup(groupId)) {
            OrganizationController orgaController = jobDomainSC.getOrganisationController();
            Group group = orgaController.getGroup(groupId);
            String domainId = group.getDomainId();
            if (domainId == null) {
              domainId = "-1";
            }

            // not refresh the domain
            jobDomainSC.setRefreshDomain(false);

            // domaine
            jobDomainSC.setTargetDomain(domainId);
            jobDomainSC.returnIntoGroup(null);

            // groupe(s) père(s)
            List<String> groupList = orgaController.getPathToGroup(groupId);
            for (String elementGroupId : groupList) {
              jobDomainSC.goIntoGroup(elementGroupId);
            }

            // groupe
            jobDomainSC.goIntoGroup(groupId);

            destination = GROUP_CONTENT_DEST;
          } else {
            destination = "/admin/jsp/accessForbidden.jsp";
          }
        } else if ("groupViewRights".equals(function)) {
          request.setAttribute("GroupProfiles", jobDomainSC.getCurrentProfiles());
        }

        if (destination.length() <= 0) {
          if (jobDomainSC.getTargetGroup() != null) {
            if (bHaveToRefreshDomain) {
              reloadDomainNavigation(request);
            }
            destination = GROUP_CONTENT_DEST;
          } else if (bHaveToRefreshDomain) {
            destination = getDestination("domainRefresh", jobDomainSC, request);
          } else {
            destination = getDestination(DOMAIN_CONTENT_FCT, jobDomainSC, request);
          }
        }
        // DOMAIN Actions --------------------------------------------
      } else if (function.startsWith(DOMAIN_ATTR)) {
        jobDomainSC.setTargetUser(null);
        if (function.startsWith("domainModifyUserFilter")) {
          destination = handleUserFilterModification(jobDomainSC, request);
        } else if (function.startsWith("domainGoTo")) {
          jobDomainSC.setTargetDomain(request.getParameter(IDDOMAIN_PARAM));
          jobDomainSC.returnIntoGroup(null);
          jobDomainSC.setRefreshDomain(true);
          return emptyJsonResponse();
        } else if (function.startsWith("domainNavigation")) {
          jobDomainSC.setTargetDomain(request.getParameter(IDDOMAIN_PARAM));
          jobDomainSC.returnIntoGroup(null);
          jobDomainSC.setRefreshDomain(true);
          destination = DOMAIN_NAVIGATION_DEST;
        } else if (function.startsWith("domainRefreshCurrentLevel")) {
          request.setAttribute("domainRefreshCurrentLevel", true);
          destination = DOMAIN_NAVIGATION_DEST;
        } else {
          if (function.startsWith(DOMAIN_CONTENT_FCT)) {
            jobDomainSC.returnIntoGroup(null);
          } else if (function.startsWith(DOMAIN_CREATE_FCT)
                    || function.startsWith(DOMAIN_SCIM_CREATE_FCT)
                    || function.startsWith(DOMAIN_GOOGLE_CREATE_FCT)) {
            final DomainType domainType;
            if (function.startsWith(DOMAIN_CREATE_FCT)) {
              domainType = DomainType.LDAP;
            } else if (function.startsWith(DOMAIN_SCIM_CREATE_FCT)) {
              domainType = DomainType.SCIM;
            } else {
              domainType = DomainType.GOOGLE;
            }
            String newDomainId = jobDomainSC.createDomain(request2Domain(request), domainType);
            request.setAttribute(IDDOMAIN_PARAM, newDomainId);
            destination = GO_BACK_DEST;
          } else if (function.startsWith(DOMAIN_SQL_CREATE_FCT)) {
            String newDomainId = jobDomainSC.createSQLDomain(WebEncodeHelper.htmlStringToJavaString(request.getParameter(DOMAIN_NAME_PARAM)),
                WebEncodeHelper.htmlStringToJavaString(request.getParameter(DOMAIN_DESCRIPTION_PARAM)),
                WebEncodeHelper.htmlStringToJavaString(request.getParameter(SILVERPEAS_SERVER_URL_PARAM)),request.getParameter(USER_DOMAIN_QUOTA_MAX_COUNT_PARAM));
            request.setAttribute(IDDOMAIN_PARAM, newDomainId);
            destination = GO_BACK_DEST;
          } else if (function.startsWith("domainModify")) {
            String modifiedDomainId = jobDomainSC.modifyDomain(request2Domain(request),
                    request.getParameter(USER_DOMAIN_QUOTA_MAX_COUNT_PARAM));
            request.setAttribute(IDDOMAIN_PARAM, modifiedDomainId);
            destination = GO_BACK_DEST;
          } else if (function.startsWith("domainSQLModify")) {
            String modifiedDomainId = jobDomainSC.modifySQLDomain(WebEncodeHelper.
                    htmlStringToJavaString(request.getParameter(DOMAIN_NAME_PARAM)), WebEncodeHelper.htmlStringToJavaString(request.getParameter(
                DOMAIN_DESCRIPTION_PARAM)),
                WebEncodeHelper.htmlStringToJavaString(request.getParameter(
                    SILVERPEAS_SERVER_URL_PARAM)),
                request.getParameter(USER_DOMAIN_QUOTA_MAX_COUNT_PARAM));
            request.setAttribute(IDDOMAIN_PARAM, modifiedDomainId);
            destination = GO_BACK_DEST;
          } else if (function.startsWith(DOMAIN_DELETE_FCT)) {
            jobDomainSC.deleteDomain(DomainType.LDAP);
            destination = GO_BACK_DEST;
          } else if (function.startsWith(DOMAIN_SCIM_DELETE_FCT)) {
            jobDomainSC.deleteDomain(DomainType.SCIM);
            destination = GO_BACK_DEST;
          } else if (function.startsWith(DOMAIN_GOOGLE_DELETE_FCT)) {
            jobDomainSC.deleteDomain(DomainType.GOOGLE);
            destination = GO_BACK_DEST;
          } else if (function.startsWith(DOMAIN_SQL_DELETE_FCT)) {
            jobDomainSC.deleteSQLDomain();
            destination = GO_BACK_DEST;
          } else if (function.startsWith("domainPingSynchro")) {
            if (jobDomainSC.isEnCours()) {
              destination = DOMAIN_SYNCHRO_PING_DEST;
            } else {
              String strSynchroReport = jobDomainSC.getSynchroReport();

              jobDomainSC.refresh();

              request.setAttribute("SynchroDomainReport", strSynchroReport);
              destination = "domainSynchroReport.jsp";
            }
          } else if (function.startsWith("domainSynchro")) {
            jobDomainSC.synchroDomain(Level.valueOf(request.getParameter("IdTraceLevel")));
            destination = DOMAIN_SYNCHRO_PING_DEST;
          } else if (function.startsWith("domainSQLSynchro")) {
            jobDomainSC.synchroSQLDomain();
            destination = DOMAIN_SYNCHRO_PING_DEST;
          } else if (function.startsWith("domainRefresh")) {
            request.setAttribute(IDDOMAIN_PARAM, jobDomainSC.getTargetDomain().getId());
            destination = GO_BACK_DEST;
          }

          if (destination.length() <= 0) {
            if (jobDomainSC.getTargetDomain() != null) {
              destination = DOMAIN_CONTENT_DEST;
            } else {
              destination = getDestination("welcome", jobDomainSC, request);
            }
          }
        }
      } else if (function.startsWith("display")) {
        if (function.startsWith("displayGroupCreate")) {
          GroupDetail newGroup = new GroupDetail();

          newGroup.setSuperGroupId(request.getParameter(IDGROUP_PARAM));
          request.setAttribute(GROUP_OBJECT_ATTR, newGroup);
          request.setAttribute(ACTION_ATTR, "groupCreate");
          request.setAttribute(GROUPS_PATH_ATTR, jobDomainSC
              .getPath((String) request.getAttribute(MY_COMPONENT_URL_ATTR),
                  jobDomainSC.getString("JDP.groupAdd") + "..."));
          destination = "groupCreate.jsp";
        } else if (function.startsWith("displayGroupUpdate")) {
          request.setAttribute(GROUP_OBJECT_ATTR, jobDomainSC.getTargetGroup());
          request.setAttribute(ACTION_ATTR, "groupUpdate");
          request.setAttribute(GROUPS_PATH_ATTR, jobDomainSC
              .getPath((String) request.getAttribute(MY_COMPONENT_URL_ATTR),
                  jobDomainSC.getString("JDP.groupUpdate") + "..."));
          destination = "groupCreate.jsp";
        } else if (function.startsWith("displayGroupImport")) {
          request.setAttribute(GROUPS_PATH_ATTR, jobDomainSC
              .getPath((String) request.getAttribute(MY_COMPONENT_URL_ATTR),
                  jobDomainSC.getString("JDP.groupImport") + "..."));
          destination = "groupImport.jsp";
        } else if (function.startsWith("displaySelectUserOrGroup")) {
          destination = jobDomainSC.initSelectionPeasForOneGroupOrUser((String) request.
              getAttribute(MY_COMPONENT_URL_ATTR));
        } else if (function.startsWith("displayAddRemoveUsers")) {
          destination = jobDomainSC
              .initSelectionPeasForGroups((String) request.getAttribute(MY_COMPONENT_URL_ATTR));
        } else if (function.startsWith("displayUserCreate")) {
          DomainDriverManager domainDriverManager =
              DomainDriverManagerProvider.getCurrentDomainDriverManager();
          DomainDriver domainDriver =
              domainDriverManager.getDomainDriver(jobDomainSC.getTargetDomain().getId());
          UserFull newUser = new UserFull(domainDriver);
          newUser.setPasswordAvailable(true);

          request.setAttribute(USER_OBJECT_ATTR, newUser);
          request.setAttribute(ACTION_ATTR, "userCreate");
          request.setAttribute(GROUPS_PATH_ATTR,
              jobDomainSC.getPath((String) request.getAttribute(MY_COMPONENT_URL_ATTR),
              jobDomainSC.getString("JDP.userAdd") + "..."));
          request.setAttribute(MIN_LENGTH_LOGIN_ATTR, jobDomainSC.getMinLengthLogin());
          request.setAttribute(CURRENT_USER_ATTR, jobDomainSC.getUserDetail());
          // if community management is activated, add groups on this user is manager
          if (JobDomainSettings.m_UseCommunityManagement) {
            request.setAttribute("GroupsManagedByCurrentUser", jobDomainSC.getUserManageableGroups());
          }

          destination = USER_CREATE_DEST;
        } else if (function.startsWith("displayUsersCsvImport")) {
          request.setAttribute(GROUPS_PATH_ATTR, jobDomainSC
              .getPath((String) request.getAttribute(MY_COMPONENT_URL_ATTR),
                  jobDomainSC.getString("JDP.csvImport") + "..."));
          request.setAttribute("FieldLabelsToImport", jobDomainSC.getFieldLabelsOfCSVToImport());
          destination = "usersCsvImport.jsp";
        } else if (function.startsWith("displayUserUpdate")) {
          request.setAttribute(USER_OBJECT_ATTR, jobDomainSC.getTargetUserFull());
          request.setAttribute(ACTION_ATTR, "userUpdate");
          request.setAttribute(GROUPS_PATH_ATTR, jobDomainSC
              .getPath((String) request.getAttribute(MY_COMPONENT_URL_ATTR),
                  jobDomainSC.getString("JDP.userUpdate") + "..."));
          request.setAttribute(MIN_LENGTH_LOGIN_ATTR, jobDomainSC.getMinLengthLogin());
          request.setAttribute(CURRENT_USER_ATTR, jobDomainSC.getUserDetail());

          destination = USER_CREATE_DEST;
        } else if (function.startsWith("displayUserMS")) {
          request.setAttribute(USER_OBJECT_ATTR, jobDomainSC.getTargetUserFull());
          request.setAttribute(ACTION_ATTR, "userMS");
          request.setAttribute(GROUPS_PATH_ATTR, jobDomainSC
              .getPath((String) request.getAttribute(MY_COMPONENT_URL_ATTR),
                  jobDomainSC.getString("JDP.userUpdate") + "..."));
          request.setAttribute(MIN_LENGTH_LOGIN_ATTR, jobDomainSC.getMinLengthLogin());
          request.setAttribute(CURRENT_USER_ATTR, jobDomainSC.getUserDetail());

          destination = USER_CREATE_DEST;
        } else if (function.startsWith(DISPLAY_USER_IMPORT_FCT)) {
          request.setAttribute("SelectedIds", jobDomainSC.getListSelectedUsers());
          request.setAttribute("FirstUserIndex", jobDomainSC.
              getIndexOfFirstItemToDisplay());
          request.setAttribute(GROUPS_PATH_ATTR, jobDomainSC
              .getPath((String) request.getAttribute(MY_COMPONENT_URL_ATTR),
                  jobDomainSC.getString("JDP.userImport") + "..."));
          request.setAttribute("properties", jobDomainSC.getPropertiesToImport());
          destination = "userImport.jsp";
        } else if (function.startsWith("displayDomainCreate")) {
          Domain theNewDomain = new Domain();
          theNewDomain.setDriverClassName("org.silverpeas.core.admin.domain.driver.ldapdriver.LDAPDriver");
          theNewDomain.setPropFileName("org.silverpeas.domains.domain");
          theNewDomain.setAuthenticationServer("autDomain");
          theNewDomain.setSilverpeasServerURL(URLUtil.getAbsoluteApplicationURL());
          request.setAttribute(DOMAIN_OBJECT_ATTR, theNewDomain);
          request.setAttribute(ACTION_ATTR, DOMAIN_CREATE_FCT);
          destination = DOMAIN_CREATE_DEST;
        } else if (function.startsWith("displayDomainSCIMCreate")) {
          Domain theNewDomain = new Domain();
          theNewDomain.setDriverClassName("org.silverpeas.core.admin.domain.driver.scimdriver.SCIMDriver");
          theNewDomain.setPropFileName("org.silverpeas.domains.domainSCIM");
          theNewDomain.setAuthenticationServer("autDomainSCIM");
          theNewDomain.setSilverpeasServerURL(URLUtil.getAbsoluteApplicationURL());
          request.setAttribute(DOMAIN_OBJECT_ATTR, theNewDomain);
          request.setAttribute(ACTION_ATTR, DOMAIN_SCIM_CREATE_FCT);
          destination = DOMAIN_CREATE_DEST;
        } else if (function.startsWith("displayDomainGoogleCreate")) {
          Domain theNewDomain = new Domain();
          theNewDomain.setDriverClassName("org.silverpeas.core.admin.domain.driver.googledriver.GoogleDriver");
          theNewDomain.setPropFileName("org.silverpeas.domains.domainGoogle");
          theNewDomain.setAuthenticationServer("autDomainGoogle");
          theNewDomain.setSilverpeasServerURL(URLUtil.getAbsoluteApplicationURL());
          request.setAttribute(DOMAIN_OBJECT_ATTR, theNewDomain);
          request.setAttribute(ACTION_ATTR, DOMAIN_GOOGLE_CREATE_FCT);
          destination = DOMAIN_CREATE_DEST;
        } else if (function.startsWith("displayDomainSQLCreate")) {
          Domain theNewDomain = new Domain();
          theNewDomain.setSilverpeasServerURL(URLUtil.getAbsoluteApplicationURL());
          request.setAttribute(DOMAIN_OBJECT_ATTR, theNewDomain);
          request.setAttribute(ACTION_ATTR, DOMAIN_SQL_CREATE_FCT);
          destination = "domainSQLCreate.jsp";
        } else if (function.startsWith("displayDomainModify")) {
          request.setAttribute(ACTION_ATTR, "domainModify");
          destination = DOMAIN_CREATE_DEST;
        } else if (function.startsWith("displayDomainSQLModify")) {
          request.setAttribute(ACTION_ATTR, "domainSQLModify");
          destination = "domainSQLCreate.jsp";
        } else if (function.startsWith("displayDomainSynchro")) {
          destination = "domainSynchro.jsp";
        } else if (function.startsWith("displayDynamicSynchroReport")) {
          SynchroDomainReport.setReportLevel(Level.valueOf(request.getParameter("IdTraceLevel")));
          destination = "dynamicSynchroReport.jsp";
        } else if (function.startsWith(DISPLAY_REMOVED_USERS_DEST)) {
          final SilverpeasList<UserDetail> removedUsers = SilverpeasList.wrap(jobDomainSC.getRemovedUsers());
          request.setAttribute("removedUsers", convertRemovedUserList(removedUsers, emptySet()));
          request.setAttribute(DOMAIN_ATTR, jobDomainSC.getTargetDomain());
          request.setAttribute(THE_USER_ATTR, jobDomainSC.getUserDetail());
          destination = "removedUsers.jsp";
        } else if (function.startsWith("displayDeletedUsers")) {
          final List<UserDetail> deletedUsers = jobDomainSC.getDeletedUsers();
          request.setAttribute("deletedUsers", deletedUsers);
          request.setAttribute(DOMAIN_ATTR, jobDomainSC.getTargetDomain());
          request.setAttribute(THE_USER_ATTR, jobDomainSC.getUserDetail());
          destination = "deletedUsers.jsp";
        } else if (function.startsWith(DISPLAY_REMOVED_GROUPS_DEST)) {
          final List<GroupDetail> allRemovedGroups = jobDomainSC.getRemovedGroups();
          final SilverpeasList<GroupDetail> removedGroups = SilverpeasList.wrap(allRemovedGroups);
          request.setAttribute("removedGroups", convertRemovedGroupList(removedGroups, emptySet()));
          request.setAttribute(DOMAIN_ATTR, jobDomainSC.getTargetDomain());
          request.setAttribute(THE_USER_ATTR, jobDomainSC.getUserDetail());
          destination = "removedGroups.jsp";
        }
      } else if (function.startsWith("welcome")) {
        jobDomainSC.returnIntoGroup(null);
        request.setAttribute("DisplayOperations", jobDomainSC.getUserDetail().isAccessAdmin());

        SettingBundle rs = getSettingBundle("org.silverpeas.jobDomainPeas.settings.jobDomainPeasSettings");
        Properties configuration = new Properties();
        configuration
            .setProperty(SilverpeasTemplate.TEMPLATE_ROOT_DIR, rs.getString("templatePath"));
        configuration.setProperty(SilverpeasTemplate.TEMPLATE_CUSTOM_DIR, rs.getString("customersTemplatePath"));
        SilverpeasTemplate template = SilverpeasTemplateFactory.createSilverpeasTemplate(configuration);

        // setting domains to welcome template
        List<Domain> allDomains = jobDomainSC.getAllDomains();
        // do not return mixed domain
        String[] domainsByList = new String[allDomains.size() - 1];
        for (int n = 1; n < allDomains.size(); n++) {
          domainsByList[n - 1] = allDomains.get(n).getName();
        }
        template.setAttribute("listDomains", domainsByList);
        request.setAttribute("Content",
            template.applyFileTemplate("register_" + jobDomainSC.getLanguage()));

        destination = "welcome.jsp";
      } else if ("Pagination".equals(function)) {
        processSelection(request, jobDomainSC);

        // traitement de la pagination : passage des parametres
        String index = request.getParameter("Pagination_Index");

        if (index != null && index.length() > 0) {
          jobDomainSC.setIndexOfFirstItemToDisplay(index);
        }
        // retour a l'album courant
        request.setAttribute("Query", jobDomainSC.getQueryToImport());
        request.setAttribute(USERS_ATTR, jobDomainSC.getUsersToImport());
        destination = getDestination(DISPLAY_USER_IMPORT_FCT, jobDomainSC, request);
      } else if ("SelectRightsUserOrGroup".equals(function)) {
        destination = jobDomainSC.initSelectionRightsUserOrGroup();
      } else if ("AssignSameRights".equals(function)) {
        if (!jobDomainSC.isRightCopyReplaceEnabled()) {
          throwHttpForbiddenError();
        }
        //1 = replace rights | 2 = add rights
        String choiceAssignRights = request.getParameter("choiceAssignRights");
        String sourceRightsId = request.getParameter("sourceRightsId");
        //Set | Element
        String sourceRightsType = request.getParameter("sourceRightsType");
        //true | false
        boolean nodeAssignRights = request.getParameterAsBoolean("nodeAssignRights");

        jobDomainSC
              .assignRights(choiceAssignRights, sourceRightsId, sourceRightsType, nodeAssignRights);

        if (jobDomainSC.getTargetUserDetail() != null) {
          destination = USER_CONTENT_DEST;
        } else {
          destination = GROUP_CONTENT_DEST;
        }
      } else {
        destination = function;
      }

      // 2) Prepare the pages
      // --------------------
      if (jobDomainSC.getTargetDomain() != null) {
        request.setAttribute(DOMAIN_OBJECT_ATTR, jobDomainSC.getTargetDomain());
      }
      if (DOMAIN_CONTENT_DEST.equals(destination)) {
        jobDomainSC.refresh();
        long domainRight = jobDomainSC.getDomainActions();
        request.setAttribute(THE_USER_ATTR, jobDomainSC.getUserDetail());
        request.setAttribute("subGroups", jobDomainSC.getSubGroups(false));
        request.setAttribute("subUsers", jobDomainSC.getSubUsers(false));
        setRightManagementAttributes(request, domainRight);

        request.setAttribute(IS_ONLY_GROUP_MANAGER_ATTR, jobDomainSC.isOnlyGroupManager());
        request.setAttribute(IS_ONLY_SPACE_MANAGER_ATTR, jobDomainSC.isOnlySpaceManager());
        request.setAttribute("isUserAddingAllowedForGroupManager", jobDomainSC.
            isUserAddingAllowedForGroupManager());
      } else if (GROUP_CONTENT_DEST.equals(destination) || "exportgroup.jsp".equals(destination)) {
        long domainRight = jobDomainSC.getDomainActions();

        request.setAttribute(GROUP_OBJECT_ATTR, jobDomainSC.getTargetGroup());
        request.setAttribute(GROUPS_PATH_ATTR,
            jobDomainSC.getPath((String) request.getAttribute(MY_COMPONENT_URL_ATTR), null));
        request.setAttribute("subGroups", jobDomainSC.getSubGroups(true));
        request.setAttribute("subUsers", jobDomainSC.getSubUsers(true));
        setRightManagementAttributes(request, domainRight);

        request
            .setAttribute("isGroupManagerOnThisGroup", jobDomainSC.isGroupManagerOnCurrentGroup());
        request.setAttribute("isGroupManagerDirectlyOnThisGroup", jobDomainSC.
            isGroupManagerDirectlyOnCurrentGroup());
        request.setAttribute(IS_ONLY_GROUP_MANAGER_ATTR, jobDomainSC.isOnlyGroupManager());
        request.setAttribute(IS_ONLY_SPACE_MANAGER_ATTR, jobDomainSC.isOnlySpaceManager());

        request.setAttribute("ManageableSpaces", jobDomainSC.getManageablesSpaces());
        request.setAttribute("IsRightCopyReplaceEnabled",
            jobDomainSC.isRightCopyReplaceEnabled());
      } else if (USER_CONTENT_DEST.equals(destination)) {
        request.setAttribute(GROUPS_PATH_ATTR,
            jobDomainSC.getPath((String) request.getAttribute(MY_COMPONENT_URL_ATTR), null));

        if (jobDomainSC.getTargetDomain() != null) {
          long domainRight = jobDomainSC.getDomainActions();
          setRightManagementAttributes(request, domainRight);
          request.setAttribute("isX509Enabled", (domainRight & ACTION_X509_USER) != 0);
          request.setAttribute(IS_ONLY_GROUP_MANAGER_ATTR, jobDomainSC.isOnlyGroupManager());
          request.setAttribute("userManageableByGroupManager", jobDomainSC.
              isUserInAtLeastOneGroupManageableByCurrentUser());
          request.setAttribute(IS_ONLY_SPACE_MANAGER_ATTR, jobDomainSC.isOnlySpaceManager());
        }
        try {
          request.setAttribute(USER_OBJECT_ATTR, jobDomainSC.getTargetUserFull());
        } catch (JobDomainPeasException e) {
          request.setAttribute(USER_OBJECT_ATTR, jobDomainSC.getTargetUserDetail());
        }
        request.setAttribute("Index", jobDomainSC.getIndex());
        request.setAttribute("UserGroups", jobDomainSC.getCurrentUserGroups());
        request.setAttribute("UserManageableSpaces", jobDomainSC.getManageablesSpaces());
        request.setAttribute("UserManageableGroups", jobDomainSC.getManageablesGroups());
        request.setAttribute("IsRightCopyReplaceEnabled",
            jobDomainSC.isRightCopyReplaceEnabled());

      } else if (DOMAIN_NAVIGATION_DEST.equals(destination)) {
        List<Domain> domains = jobDomainSC.getAllDomains();
        if (domains.size() == 1) {
          jobDomainSC.setTargetDomain(domains.get(0).getId());
        }
        request.setAttribute("allDomains", domains);
        request.setAttribute("allRootGroups", jobDomainSC.getAllRootGroups());
        request.setAttribute("CurrentDomain", jobDomainSC.getTargetDomain());
      } else if ("groupManagers.jsp".equals(destination)) {
        request.setAttribute(GROUP_OBJECT_ATTR, jobDomainSC.getTargetGroup());
        request.setAttribute(GROUPS_PATH_ATTR,
            jobDomainSC.getPath((String) request.getAttribute(MY_COMPONENT_URL_ATTR), null));
      }
      // 3) Concat the path
      // ------------------
      if (!destination.startsWith("/")) {
        destination = "/jobDomainPeas/jsp/" + destination;
      }
    } catch (Exception e) {
      request.setAttribute("javax.servlet.jsp.jspException", e);
      destination = "/admin/jsp/errorpageMain.jsp";
    }


    return destination;
  }
}

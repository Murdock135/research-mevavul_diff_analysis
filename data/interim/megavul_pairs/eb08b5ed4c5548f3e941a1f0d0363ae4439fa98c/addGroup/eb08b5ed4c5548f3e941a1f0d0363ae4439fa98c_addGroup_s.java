class addGroup {
private ModelAndView addGroup(HttpServletRequest request, HttpServletResponse response) throws Exception {    
                
        String groupName = request.getParameter("groupName");
        String groupComment = request.getParameter("groupComment");
        if (groupComment == null) {
            groupComment = "";
        }

        boolean hasGroup = false;
        try {
            hasGroup = m_groupRepository.groupExists(groupName);
        } catch (Throwable e) {
            throw new ServletException("Can't determine if group " + groupName + " already exists in groups.xml.", e);
        }
        
        if (hasGroup) {
            return new ModelAndView("admin/userGroupView/groups/newGroup", "action", "redo");            
        } else {
            WebGroup newGroup = new WebGroup();
            newGroup.setName(groupName);
            newGroup.setComments(groupComment);
            
            return editGroup(request, newGroup);
        }
    }
}

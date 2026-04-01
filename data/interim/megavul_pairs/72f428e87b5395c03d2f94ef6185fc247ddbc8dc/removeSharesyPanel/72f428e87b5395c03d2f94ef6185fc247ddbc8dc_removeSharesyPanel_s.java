class removeSharesyPanel {
public void removeSharesyPanel(String panelId) {
        PanelGroup panelGroup = panelGroupMapper.selectByPrimaryKey(panelId);
        PanelShareRemoveRequest request = new PanelShareRemoveRequest();
        request.setPanelId(panelId);
        List<PanelShareOutDTO> panelShareOutDTOS = queryTargets(panelId);
        extPanelShareMapper.removeShares(request);
        if (CollectionUtils.isEmpty(panelShareOutDTOS) || ObjectUtils.isEmpty(panelGroup) || StringUtils.isBlank(panelGroup.getName())) {
            return;
        }
        panelShareOutDTOS.forEach(shareOut -> {
            SysLogConstants.SOURCE_TYPE buiType = buiType(shareOut.getType());
            DeLogUtils.save(SysLogConstants.OPERATE_TYPE.UNSHARE, SysLogConstants.SOURCE_TYPE.PANEL, panelId, panelGroup.getPid(), shareOut.getTargetId(), buiType);
        });

        Map<Integer, List<PanelShareOutDTO>> listMap = panelShareOutDTOS.stream().collect(Collectors.groupingBy(dto -> dto.getType()));
        AuthURD urd = new AuthURD();
        for (Map.Entry<Integer, List<PanelShareOutDTO>> entry : listMap.entrySet()) {
            List<PanelShareOutDTO> dtoList = entry.getValue();
            if (CollectionUtils.isNotEmpty(dtoList)) {
                List<Long> curTargetIds = dtoList.stream().map(dto -> Long.parseLong(dto.getTargetId())).collect(Collectors.toList());
                buildRedAuthURD(entry.getKey(), curTargetIds, urd);
            }
        }
        Set<Long> userIds = AuthUtils.userIdsByURD(urd);
        if (CollectionUtils.isNotEmpty(userIds)) {
            CurrentUserDto user = AuthUtils.getUser();
            Gson gson = new Gson();
            userIds.forEach(userId -> {
                if (!user.getUserId().equals(userId)) {
                    String msg = panelGroup.getName();
                    List<String> msgParam = new ArrayList<>();
                    msgParam.add(panelId);
                    DeMsgutil.sendMsg(userId, 3L, user.getNickName() + " 取消分享了仪表板【" + msg + "】，请查收!", gson.toJson(msgParam));
                }
            });
        }
    }
}

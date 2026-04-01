class update {
@ApiOperation(value = "修改", notes = "修改")
    @ResponseBody
    @RequestMapping("/update")
    @RequiresPermissions("novel:friendLink:edit")
    public R update(FriendLinkDO friendLink) {
        friendLinkService.update(friendLink);
        redisTemplate.delete(CacheKey.INDEX_LINK_KEY);
        return R.ok();
    }
}

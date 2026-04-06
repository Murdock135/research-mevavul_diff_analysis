class save {
@ApiOperation(value = "新增", notes = "新增")
    @ResponseBody
    @PostMapping("/save")
    @RequiresPermissions("novel:friendLink:add")
    public R save(FriendLinkDO friendLink) {
        if (friendLinkService.save(friendLink) > 0) {
            redisTemplate.delete(CacheKey.INDEX_LINK_KEY);
            return R.ok();
        }
        return R.error();
    }
}

class getQueueSizePerType {
public Map<String, Long> getQueueSizePerType()
    {
        return this.taskManager.getQueueSizePerType(this.xcontextProvider.get().getWikiId());
    }
}

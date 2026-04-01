class createHandler {
@Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (thingTypeUID.equals(THING_COMMAND)) {
            return new ExecHandler(thing, execWhitelistWatchService);
        }

        return null;
    }
}

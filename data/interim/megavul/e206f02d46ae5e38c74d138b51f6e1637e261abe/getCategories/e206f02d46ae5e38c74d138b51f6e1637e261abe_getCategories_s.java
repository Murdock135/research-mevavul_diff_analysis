class getCategories {
public static List<DashboardCategory> getCategories(Context context,
            HashMap<Pair<String, String>, Tile> cache) {
        final long startTime = System.currentTimeMillis();
        ArrayList<Tile> tiles = new ArrayList<>();
        UserManager userManager = UserManager.get(context);
        for (UserHandle user : userManager.getUserProfiles()) {
            // TODO: Needs much optimization, too many PM queries going on here.
            if (user.getIdentifier() == ActivityManager.getCurrentUser()) {
                // Only add Settings for this user.
                getTilesForAction(context, user, SETTINGS_ACTION, cache, null, tiles, true);
                getTilesForAction(context, user, OPERATOR_SETTINGS, cache,
                        OPERATOR_DEFAULT_CATEGORY, tiles, false);
                getTilesForAction(context, user, MANUFACTURER_SETTINGS, cache,
                        MANUFACTURER_DEFAULT_CATEGORY, tiles, false);
            }
            getTilesForAction(context, user, EXTRA_SETTINGS_ACTION, cache, null, tiles, false);
        }
        HashMap<String, DashboardCategory> categoryMap = new HashMap<>();
        for (Tile tile : tiles) {
            DashboardCategory category = categoryMap.get(tile.category);
            if (category == null) {
                category = createCategory(context, tile.category);
                if (category == null) {
                    Log.w(LOG_TAG, "Couldn't find category " + tile.category);
                    continue;
                }
                categoryMap.put(category.key, category);
            }
            category.addTile(tile);
        }
        ArrayList<DashboardCategory> categories = new ArrayList<>(categoryMap.values());
        for (DashboardCategory category : categories) {
            Collections.sort(category.tiles, TILE_COMPARATOR);
        }
        Collections.sort(categories, CATEGORY_COMPARATOR);
        if (DEBUG_TIMING) Log.d(LOG_TAG, "getCategories took "
                + (System.currentTimeMillis() - startTime) + " ms");
        return categories;
    }
}

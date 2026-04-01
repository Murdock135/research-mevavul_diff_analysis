class makePopUp {
@Nonnull
  public static JPopupMenu makePopUp(
          @Nonnull final MindMapPanel source,
          final boolean fullScreenModeActive,
          @Nonnull final DialogProvider dialogProvider,
          @Nullable final Topic topicUnderMouse,
          @Nonnull @MustNotContainNull final Topic[] selectedTopics,
          @Nonnull Map<Class<? extends PopUpMenuItemPlugin>, CustomJob> customProcessors
  ) {
    final JPopupMenu result = UI_COMPO_FACTORY.makePopupMenu();
    final List<PopUpMenuItemPlugin> pluginMenuItems = MindMapPluginRegistry.getInstance().findFor(PopUpMenuItemPlugin.class);
    final List<JMenuItem> tmpList = new ArrayList<JMenuItem>();

    final boolean isModelNotEmpty = source.getModel().getRoot() != null;

    putAllItemsAsSection(result, null, findPopupMenuItems(source, PopUpSection.MAIN, fullScreenModeActive, tmpList, dialogProvider, topicUnderMouse, selectedTopics, pluginMenuItems, customProcessors));
    putAllItemsAsSection(result, null, findPopupMenuItems(source, PopUpSection.MANIPULATORS, fullScreenModeActive, tmpList, dialogProvider, topicUnderMouse, selectedTopics, pluginMenuItems, customProcessors));
    putAllItemsAsSection(result, null, findPopupMenuItems(source, PopUpSection.EXTRAS, fullScreenModeActive, tmpList, dialogProvider, topicUnderMouse, selectedTopics, pluginMenuItems, customProcessors));

    final JMenu exportMenu = UI_COMPO_FACTORY.makeMenu(BUNDLE.getString("MMDExporters.SubmenuName"));
    exportMenu.setIcon(ICON_SERVICE.getIconForId(IconID.POPUP_EXPORT));

    final JMenu importMenu = UI_COMPO_FACTORY.makeMenu(BUNDLE.getString("MMDImporters.SubmenuName"));
    importMenu.setIcon(ICON_SERVICE.getIconForId(IconID.POPUP_IMPORT));

    putAllItemsAsSection(result, importMenu, findPopupMenuItems(source, PopUpSection.IMPORT, fullScreenModeActive, tmpList, dialogProvider, topicUnderMouse, selectedTopics, pluginMenuItems, customProcessors));
    if (isModelNotEmpty) {
      putAllItemsAsSection(result, exportMenu, findPopupMenuItems(source, PopUpSection.EXPORT, fullScreenModeActive, tmpList, dialogProvider, topicUnderMouse, selectedTopics, pluginMenuItems, customProcessors));
    }

    putAllItemsAsSection(result, null, findPopupMenuItems(source, PopUpSection.TOOLS, fullScreenModeActive, tmpList, dialogProvider, topicUnderMouse, selectedTopics, pluginMenuItems, customProcessors));
    putAllItemsAsSection(result, null, findPopupMenuItems(source, PopUpSection.MISC, fullScreenModeActive, tmpList, dialogProvider, topicUnderMouse, selectedTopics, pluginMenuItems, customProcessors));

    return result;
  }
}

class onCreate {
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIsEmbeddingActivityEnabled = ActivityEmbeddingUtils.isEmbeddingActivityEnabled(this);
        if (mIsEmbeddingActivityEnabled) {
            final UserManager um = getSystemService(UserManager.class);
            final UserInfo userInfo = um.getUserInfo(getUser().getIdentifier());
            if (userInfo.isManagedProfile()) {
                final Intent intent = new Intent(getIntent())
                        .setClass(this, DeepLinkHomepageActivityInternal.class)
                        .addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT)
                        .putExtra(EXTRA_USER_HANDLE, getUser());
                intent.removeFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityAsUser(intent, um.getPrimaryUser().getUserHandle());
                finish();
                return;
            }
        }

        setupEdgeToEdge();
        setContentView(R.layout.settings_homepage_container);

        mSplitController = SplitController.getInstance();
        mIsTwoPane = mSplitController.isActivityEmbedded(this);

        updateAppBarMinHeight();
        initHomepageContainer();
        updateHomepageAppBar();
        updateHomepageBackground();
        mLoadedListeners = new ArraySet<>();

        initSearchBarView();

        getLifecycle().addObserver(new HideNonSystemOverlayMixin(this));
        mCategoryMixin = new CategoryMixin(this);
        getLifecycle().addObserver(mCategoryMixin);

        final String highlightMenuKey = getHighlightMenuKey();
        // Only allow features on high ram devices.
        if (!getSystemService(ActivityManager.class).isLowRamDevice()) {
            initAvatarView();
            final boolean scrollNeeded = mIsEmbeddingActivityEnabled
                    && !TextUtils.equals(getString(DEFAULT_HIGHLIGHT_MENU_KEY), highlightMenuKey);
            showSuggestionFragment(scrollNeeded);
            if (FeatureFlagUtils.isEnabled(this, FeatureFlags.CONTEXTUAL_HOME)) {
                showFragment(() -> new ContextualCardsFragment(), R.id.contextual_cards_content);
                ((FrameLayout) findViewById(R.id.main_content))
                        .getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
            }
        }
        mMainFragment = showFragment(() -> {
            final TopLevelSettings fragment = new TopLevelSettings();
            fragment.getArguments().putString(SettingsActivity.EXTRA_FRAGMENT_ARG_KEY,
                    highlightMenuKey);
            return fragment;
        }, R.id.main_content);

        // Launch the intent from deep link for large screen devices.
        launchDeepLinkIntentToRight();
        updateHomepagePaddings();
        updateSplitLayout();
    }
}

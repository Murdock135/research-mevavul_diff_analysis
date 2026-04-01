class updateActionViews {
private void updateActionViews(int menuState, Rect stackBounds) {
        ViewGroup expandContainer = findViewById(R.id.expand_container);
        ViewGroup actionsContainer = findViewById(R.id.actions_container);
        actionsContainer.setOnTouchListener((v, ev) -> {
            // Do nothing, prevent click through to parent
            return true;
        });

        // Update the expand button only if it should show with the menu
        expandContainer.setVisibility(menuState == MENU_STATE_FULL
                ? View.VISIBLE
                : View.INVISIBLE);

        FrameLayout.LayoutParams expandedLp =
                (FrameLayout.LayoutParams) expandContainer.getLayoutParams();
        if (mActions.isEmpty() || menuState == MENU_STATE_NONE) {
            actionsContainer.setVisibility(View.INVISIBLE);

            // Update the expand container margin to adjust the center of the expand button to
            // account for the existence of the action container
            expandedLp.topMargin = 0;
            expandedLp.bottomMargin = 0;
        } else {
            actionsContainer.setVisibility(View.VISIBLE);
            if (mActionsGroup != null) {
                // Ensure we have as many buttons as actions
                final LayoutInflater inflater = LayoutInflater.from(mContext);
                while (mActionsGroup.getChildCount() < mActions.size()) {
                    final PipMenuActionView actionView = (PipMenuActionView) inflater.inflate(
                            R.layout.pip_menu_action, mActionsGroup, false);
                    mActionsGroup.addView(actionView);
                }

                // Update the visibility of all views
                for (int i = 0; i < mActionsGroup.getChildCount(); i++) {
                    mActionsGroup.getChildAt(i).setVisibility(i < mActions.size()
                            ? View.VISIBLE
                            : View.GONE);
                }

                // Recreate the layout
                final boolean isLandscapePip = stackBounds != null
                        && (stackBounds.width() > stackBounds.height());
                for (int i = 0; i < mActions.size(); i++) {
                    final RemoteAction action = mActions.get(i);
                    final PipMenuActionView actionView =
                            (PipMenuActionView) mActionsGroup.getChildAt(i);
                    final boolean isCloseAction = mCloseAction != null && Objects.equals(
                            mCloseAction.getActionIntent(), action.getActionIntent());

                    // TODO: Check if the action drawable has changed before we reload it
                    action.getIcon().loadDrawableAsync(mContext, d -> {
                        if (d != null) {
                            d.setTint(Color.WHITE);
                            actionView.setImageDrawable(d);
                        }
                    }, mMainHandler);
                    actionView.setCustomCloseBackgroundVisibility(
                            isCloseAction ? View.VISIBLE : View.GONE);
                    actionView.setContentDescription(action.getContentDescription());
                    if (action.isEnabled()) {
                        actionView.setOnClickListener(
                                v -> onActionViewClicked(action.getActionIntent(), isCloseAction));
                    }
                    actionView.setEnabled(action.isEnabled());
                    actionView.setAlpha(action.isEnabled() ? 1f : DISABLED_ACTION_ALPHA);

                    // Update the margin between actions
                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)
                            actionView.getLayoutParams();
                    lp.leftMargin = (isLandscapePip && i > 0) ? mBetweenActionPaddingLand : 0;
                }
            }

            // Update the expand container margin to adjust the center of the expand button to
            // account for the existence of the action container
            expandedLp.topMargin = getResources().getDimensionPixelSize(
                    R.dimen.pip_action_padding);
            expandedLp.bottomMargin = getResources().getDimensionPixelSize(
                    R.dimen.pip_expand_container_edge_margin);
        }
        expandContainer.requestLayout();
    }
}

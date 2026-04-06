class onPageStarted {
@Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
          // While the login view is open, disable the ability to do screenshots.
          m_activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);

          super.onPageStarted(view, url, favicon);
          nativeOnPageStarted(url, favicon);
        }
}

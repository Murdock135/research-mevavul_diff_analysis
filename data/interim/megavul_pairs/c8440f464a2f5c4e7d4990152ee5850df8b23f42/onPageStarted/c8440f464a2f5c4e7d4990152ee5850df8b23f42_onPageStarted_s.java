class onPageStarted {
@Override
        public void onPageStarted(WebView view, String url, Bitmap favicon)
        {
            Log.v(TAG, "Url changed: " + url);

            super.onPageStarted(view, url, favicon);
            nativeOnPageStarted(url, favicon);
        }
}

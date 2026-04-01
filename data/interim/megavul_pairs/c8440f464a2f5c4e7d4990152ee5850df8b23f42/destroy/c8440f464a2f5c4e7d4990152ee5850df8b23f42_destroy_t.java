class destroy {
public void destroy()
    {
        Log.v(TAG, "bye!");
        m_activity.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            m_webView.destroy();
            m_activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
          }
        });
    }
}

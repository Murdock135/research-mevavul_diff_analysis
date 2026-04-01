class sendRedirect {
@Override
    public void sendRedirect(String redirect) throws IOException
    {
        if (StringUtils.isBlank(redirect)) {
            // Nowhere to go to
            return;
        }
        if (StringUtils.containsAny(redirect, '\r', '\n')) {
            LOGGER.warn("Possible HTTP Response Splitting attack, attempting to redirect to [{}]", redirect);
            return;
        }
        this.response.sendRedirect(redirect);
    }
}

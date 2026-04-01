class isAccountNotFound {
@Deprecated
    public boolean isAccountNotFound()
    {
        return getContent().contains("No account is registered using this email address");
    }
}

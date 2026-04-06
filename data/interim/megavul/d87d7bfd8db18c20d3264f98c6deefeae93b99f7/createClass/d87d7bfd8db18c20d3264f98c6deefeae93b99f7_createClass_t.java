class createClass {
@Override
    protected void createClass(BaseClass xclass)
    {
        super.createClass(xclass);

        xclass.addTextAreaField("highlight", "Highlighted Text", 40, 2);
        xclass.addNumberField("replyto", "Reply To", 5, "integer");

        String commentPropertyName = "comment";
        xclass.addTextAreaField(commentPropertyName, "Comment", 40, 5, true);

        // FIXME: Ensure that the comment text editor is set to its default value after an upgrade. This should be
        // handled in a cleaner way in BaseClass#addTextAreaField. See: https://jira.xwiki.org/browse/XWIKI-17605
        TextAreaClass comment =  (TextAreaClass) xclass.getField(commentPropertyName);
        comment.setEditor((String) null);
    }
}

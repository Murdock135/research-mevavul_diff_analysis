class dialogHead {
public String dialogHead(String title) {

        String escapedTitle;
        if (title == null) {
            escapedTitle = "";
        } else {
            escapedTitle = CmsEncoder.escapeHtml(title);
        }

        return "<div class=\"dialoghead\" unselectable=\"on\">" + escapedTitle + "</div>";
    }
}

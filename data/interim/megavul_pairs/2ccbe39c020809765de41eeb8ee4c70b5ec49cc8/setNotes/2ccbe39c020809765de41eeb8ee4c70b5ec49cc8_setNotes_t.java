class setNotes {
public void setNotes(String notes) {
        this.notes = WebUtil.escapeHTML(notes);
    }
}

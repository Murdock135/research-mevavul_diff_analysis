class setHeaderCaption {
public Column setHeaderCaption(String caption)
                throws IllegalStateException {
            checkColumnIsAttached();
            if (caption == null) {
                caption = ""; // Render null as empty
            }
            state.headerCaption = caption;

            HeaderRow row = grid.getHeader().getDefaultRow();
            if (row != null) {
                row.getCell(grid.getPropertyIdByColumnId(state.id))
                        .setText(caption);
            }
            return this;
        }
}

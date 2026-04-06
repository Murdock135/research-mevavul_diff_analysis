class fireCharacterDataChanged {
protected void fireCharacterDataChanged(final CharacterDataChangeEvent event) {
        DomNode toInform = this;
        while (toInform != null) {
            final List<CharacterDataChangeListener> listeners = safeGetCharacterDataListeners();
            if (listeners != null) {
                for (final CharacterDataChangeListener listener : listeners) {
                    listener.characterDataChanged(event);
                }
            }
            toInform = toInform.getParentNode();
        }
    }
}

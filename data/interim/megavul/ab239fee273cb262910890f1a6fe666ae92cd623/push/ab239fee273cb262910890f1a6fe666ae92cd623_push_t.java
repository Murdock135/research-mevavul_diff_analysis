class push {
private void push(Context context) {
            if (++size >= limit) {
                throw new RuntimeException(JsonMessages.PARSER_INPUT_NESTED_TOO_DEEP(size));
            }
            context.next = head;
            head = context;
        }
}

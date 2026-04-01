class push {
private void push(Context context) {
            if (++size >= limit) {
                throw new RuntimeException("Input is too deeply nested " + size);
            }
            context.next = head;
            head = context;
        }
}

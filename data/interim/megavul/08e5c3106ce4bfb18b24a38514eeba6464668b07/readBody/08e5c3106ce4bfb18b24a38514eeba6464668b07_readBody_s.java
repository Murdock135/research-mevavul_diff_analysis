class readBody {
private String readBody(RoutingContext ctx) {
        if (ctx.getBody() != null) {
            return ctx.getBodyAsString();
        }
        return null;
    }
}

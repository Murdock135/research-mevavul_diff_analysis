class readBody {
private String readBody(RoutingContext ctx) {
        if (ctx.body() != null) {
            return ctx.body().asString();
        }
        return null;
    }
}

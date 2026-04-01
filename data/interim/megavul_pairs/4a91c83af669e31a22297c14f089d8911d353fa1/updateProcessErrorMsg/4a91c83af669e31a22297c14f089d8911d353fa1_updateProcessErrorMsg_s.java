class updateProcessErrorMsg {
private void updateProcessErrorMsg(Throwable e) {
        LOGGER.error(e);
        sb.append("<pre style='color:red'>").append(ExceptionUtils.recordStackTraceMsg(e)).append("</pre>");
    }
}

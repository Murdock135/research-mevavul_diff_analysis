class addField {
public UpdateSection addField(String field){
    if (! fieldPattern.matcher(field).matches()) {
      throw new IllegalArgumentException("Field name with illegal character: '" + field + "'");
    }
    fieldHierarchy.add(field);
    return this;
  }
}

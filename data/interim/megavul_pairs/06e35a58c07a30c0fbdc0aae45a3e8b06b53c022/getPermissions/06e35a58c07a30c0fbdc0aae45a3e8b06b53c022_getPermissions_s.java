class getPermissions {
default List<String> getPermissions() {
    List<String> permissions = new ArrayList<>();
    permissions.add("*:*");
    permissions.add(this.getName().replace('_', ':'));
    permissions.add(this.getName().substring(0, this.getName().indexOf('_')) + ":*");
    return permissions;
  }
}

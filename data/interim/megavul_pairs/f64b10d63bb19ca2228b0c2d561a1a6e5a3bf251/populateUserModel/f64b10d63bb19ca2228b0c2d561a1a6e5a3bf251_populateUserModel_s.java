class populateUserModel {
private void populateUserModel(String username, Entry entry, Model model)
  {
    model.addAttribute("username", username);
    for(Attribute attribute : entry.getAttributes())
    {
      model.addAttribute(attribute.getName(), attribute.getValue());
    }
    model.addAttribute("entry", entry);
  }
}

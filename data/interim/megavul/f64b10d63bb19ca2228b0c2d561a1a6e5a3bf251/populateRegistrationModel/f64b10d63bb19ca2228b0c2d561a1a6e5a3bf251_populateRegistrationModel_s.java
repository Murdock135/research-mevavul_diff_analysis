class populateRegistrationModel {
private void populateRegistrationModel(Map<String, String> parameters,
          Model model)
  {
    for(Map.Entry<String, String> parameter : parameters.entrySet())
    {
      // handle all parameters except the password
      String name = parameter.getKey();
      if(!name.equals("userPassword") && !name.equals("_csrf"))
      {
        String value = parameter.getValue().trim();
        if(!value.isEmpty())
        {
          model.addAttribute(name, value);
        }
      }
    }
  }
}

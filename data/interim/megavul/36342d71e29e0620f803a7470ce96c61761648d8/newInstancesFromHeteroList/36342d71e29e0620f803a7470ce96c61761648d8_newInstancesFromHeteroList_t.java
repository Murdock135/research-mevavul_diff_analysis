class newInstancesFromHeteroList {
public static <T extends Describable<T>>
    List<T> newInstancesFromHeteroList(StaplerRequest req, Object formData,
                Collection<? extends Descriptor<T>> descriptors) throws FormException {

        List<T> items = new ArrayList<T>();

        if (formData!=null) {
            for (Object o : JSONArray.fromObject(formData)) {
                JSONObject jo = (JSONObject)o;
                String kind = jo.getString("kind");
                Descriptor<T> d = find(descriptors, kind);
                if (d != null) {
                    items.add(d.newInstance(req, jo));
                }
            }
        }

        return items;
    }
}

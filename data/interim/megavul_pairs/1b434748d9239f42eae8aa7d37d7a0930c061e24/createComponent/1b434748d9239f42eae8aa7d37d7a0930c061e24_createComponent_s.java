class createComponent {
public UIComponent createComponent(FacesContext context, String componentType, String rendererType) {
        return createComponentApplyAnnotations(context, componentType, rendererType, true);
    }
}

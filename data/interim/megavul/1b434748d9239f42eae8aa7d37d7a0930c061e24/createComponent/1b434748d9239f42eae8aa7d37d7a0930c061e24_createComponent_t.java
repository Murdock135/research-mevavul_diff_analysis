class createComponent {
public UIComponent createComponent(FacesContext context, String componentType, String rendererType) {
        
        notNull(CONTEXT, context);
        notNull(COMPONENT_TYPE, componentType);
        
        return createComponentApplyAnnotations(context, componentType, rendererType, true);
    }
}

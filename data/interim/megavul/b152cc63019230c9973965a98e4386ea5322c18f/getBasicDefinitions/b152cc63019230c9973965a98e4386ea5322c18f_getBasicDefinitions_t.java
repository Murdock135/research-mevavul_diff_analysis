class getBasicDefinitions {
public void getBasicDefinitions(String crfVersionOID, BasicDefinitionsBean basicDef) {
        ArrayList<MeasurementUnitBean> units = basicDef.getMeasurementUnits();
        String uprev = "";
        this.setStudyMeasurementUnitsTypesExpected();
        
        //OC-17141
        HashMap<Integer, Object> param = new HashMap<Integer, Object>();        
        param.put(new Integer(1), crfVersionOID);
        ArrayList rows = this.select(getStudyMeasurementUnitsSqlbyCrfVersionOid(),param);
        
        Iterator it = rows.iterator();
        while (it.hasNext()) {
            HashMap row = (HashMap) it.next();
            String oid = (String) row.get("mu_oid");
            String name = (String) row.get("name");
            MeasurementUnitBean u = new MeasurementUnitBean();
            SymbolBean symbol = new SymbolBean();
            ArrayList<TranslatedTextBean> texts = new ArrayList<TranslatedTextBean>();
            if (uprev.equals(oid)) {
                u = units.get(units.size() - 1);
                symbol = u.getSymbol();
                texts = symbol.getTranslatedText();
            } else {
                u.setOid(oid);
                u.setName(name);
                units.add(u);
            }
            TranslatedTextBean t = new TranslatedTextBean();
            t.setText(name);
            texts.add(t);
            symbol.setTranslatedText(texts);
            u.setSymbol(symbol);
        }
    }
}

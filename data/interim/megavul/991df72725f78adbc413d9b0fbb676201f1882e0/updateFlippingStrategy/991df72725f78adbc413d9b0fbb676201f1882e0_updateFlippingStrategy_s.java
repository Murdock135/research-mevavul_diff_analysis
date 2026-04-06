class updateFlippingStrategy {
private static void updateFlippingStrategy(Feature fp, String strategy, String strategyParams) {
        
        if (null != strategy && !strategy.isEmpty()) {
            try {
                Class<?> strategyClass = Class.forName(strategy);
                FlippingStrategy fstrategy = (FlippingStrategy) strategyClass.newInstance();
               
                if (null != strategyParams && !strategyParams.isEmpty()) {
                    Map<String, String> initParams = new HashMap<String, String>();
                    String[] params = strategyParams.split(";");
                    for (String currentP : params) {
                        String[] cur = currentP.split("=");
                        if (cur.length < 2) {
                            throw new IllegalArgumentException("Invalid Syntax : param1=val1,val2;param2=val3,val4");
                        }
                        initParams.put(cur[0], cur[1]);
                    }
                    fstrategy.init(fp.getUid(), initParams);
                }
                fp.setFlippingStrategy(fstrategy);

            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Cannot find strategy class", e);
            } catch (InstantiationException e) {
                throw new IllegalArgumentException("Cannot instantiate strategy", e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Cannot instantiate : no public constructor", e);
            }
        }
    }
}

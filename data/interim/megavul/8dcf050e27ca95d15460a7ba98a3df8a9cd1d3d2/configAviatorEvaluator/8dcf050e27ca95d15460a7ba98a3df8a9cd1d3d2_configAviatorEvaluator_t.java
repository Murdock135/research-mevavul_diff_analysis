class configAviatorEvaluator {
@Bean
    public void configAviatorEvaluator() {
        AviatorEvaluatorInstance instance = AviatorEvaluator.getInstance();

        // 配置AviatorEvaluator使用LRU缓存编译后的表达式
        instance
                .useLRUExpressionCache(AVIATOR_LRU_CACHE_SIZE)
                .addFunction(new StrEqualFunction());

        // 配置Aviator语法特性集合
        instance.setOption(Options.FEATURE_SET,
                Feature.asSet(Feature.If,
                        Feature.Assignment,
                        Feature.Let,
                        Feature.StringInterpolation));

        // 配置自定义aviator函数
        instance.addOpFunction(OperatorType.BIT_OR, new AbstractFunction() {
            @Override
            public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
                                      final AviatorObject arg2) {
                try {
                    Object value1 = arg1.getValue(env);
                    Object value2 = arg2.getValue(env);
                    Object currentValue = value1 == null ? value2 : value1;
                    if (arg1.getAviatorType() == AviatorType.String) {
                        return new AviatorString(String.valueOf(currentValue));
                    } else {
                        return AviatorDouble.valueOf(currentValue);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
                return arg1.bitOr(arg2, env);
            }
            @Override
            public String getName() {
                return OperatorType.BIT_OR.getToken();
            }
        });

        instance.addFunction(new StrContainsFunction());
        instance.addFunction(new ObjectExistsFunction());
        instance.addFunction(new StrMatchesFunction());
    }
}

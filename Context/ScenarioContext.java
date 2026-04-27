package org.billing.Context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScenarioContext {
    private Map<String, String> context;

    public ScenarioContext() {
        context = new ConcurrentHashMap<>();
    }

    public void setData(String key, String value) {
        context.put(key, value);
    }

    public String getData(String key) {
        return context.get(key);
    }

    public <T> T getData(String key, Class<T> clazz) {
        return clazz.cast(context.get(key));
    }

    public void clearData() {
        context.clear();
    }
}
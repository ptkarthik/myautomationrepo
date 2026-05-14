package org.billing.Context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScenarioContextWithObject {
    private Map<String, Object> context;

    public ScenarioContextWithObject() {
        context = new ConcurrentHashMap<>();
    }

    public void setData(String key, Object value) {
        context.put(key, value);
    }

    public Object getData(String key) {
        return context.get(key);
    }

    public <T> T getData(String key, Class<T> clazz) {
        return clazz.cast(context.get(key));
    }

    public void clearData() {
        context.clear();
    }

    @Override
    public String toString() {
        return "ScenarioContextWithObject{" + "context=" + context + '}';
    }
}
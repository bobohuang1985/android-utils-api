/**********************************************************************
Copyright (c) 2009 Asfun Net.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
**********************************************************************/
package net.asfun.jangod.template;

import static net.asfun.jangod.util.logging.JangodLogger;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.asfun.jangod.base.Constants;
import net.asfun.jangod.util.logging.Level;

public class LazyBindings implements Map<String, Object> {

    private Map<String, Object> bins;
    private Set<String> keys;
    private Map<String, String> lazies;
    private Map<String, Method> methods;

    public LazyBindings() {
        bins = new HashMap<String, Object>();
        keys = new HashSet<String>();
        lazies = new HashMap<String, String>();
        methods = new HashMap<String, Method>();
    }

    /**
     * set up lazy load bindings
     * @param props
     *       like  recentPosts => net.asfun.jvalog.misc.AutoLoader.getPosts
     * @throws ScriptException
     */
    public void config(Map<String, String> props) {
        lazies = props;
        keys.addAll(props.keySet());
        bins.clear();
    }

    @Override
    public boolean containsKey(Object key) {
        return keys.contains(key);
    }

    @Override
    public Object get(Object key) {
        checkKey(key);
        if (keys.contains(key)) {
            Object value = bins.get(key);
            if (value == null) {
                value = getFromDataSource(key);
                //				bins.put(key.toString(), value);
            }
            return value;
        }
        return null;
    }

    private Object getFromDataSource(Object key) {
        try {
            String lazy = lazies.get(key);
            if (!methods.containsKey(lazy)) {
                int sep = lazy.lastIndexOf('.');
                Class<?> c = Class.forName(lazy.substring(0, sep));
                Method method = c.getDeclaredMethod(lazy.substring(sep + 1), new Class[] {});
                methods.put(lazy, method);
            }
            return methods.get(lazy).invoke(null, new Object[] {});
        } catch (Exception e) {
            JangodLogger.log(Level.SEVERE, e.getMessage(), e.getCause());
        }
        return null;
    }

    @Override
    public Object put(String name, Object value) {
        checkKey(name);
        keys.add(name);
        return bins.put(name, value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> toMerge) {
        if (toMerge == null) {
            throw new NullPointerException("toMerge map is null");
        }
        for (Map.Entry<? extends String, ? extends Object> entry : toMerge.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Object remove(Object key) {
        keys.remove(key);
        if (bins.containsKey(key)) {
            lazies.remove(key);
            return bins.remove(key);
        } else {
            Object value = getFromDataSource(key);
            lazies.remove(key);
            return value;
        }
    }

    @Override
    public void clear() {
        keys.clear();
        bins.clear();
        lazies.clear();
    }

    @Override
    public boolean containsValue(Object value) {
        return values().contains(value);
    }

    @Override
    public Set<java.util.Map.Entry<String, Object>> entrySet() {
        return bins.entrySet();
    }

    @Override
    public boolean isEmpty() {
        return keys.isEmpty();
    }

    @Override
    public Set<String> keySet() {
        return keys;
    }

    @Override
    public int size() {
        return keys.size();
    }

    @Override
    public Collection<Object> values() {
        for (String key : lazies.keySet()) {
            bins.put(key, getFromDataSource(key));
        }
        return bins.values();
    }

    private void checkKey(Object key) {
        if (key == null) {
            throw new NullPointerException("key can not be null");
        }
        if (!(key instanceof String)) {
            throw new ClassCastException("key should be a String");
        }
        if (key.equals(Constants.STR_BLANK)) {
            throw new IllegalArgumentException("key can not be empty");
        }
    }

}

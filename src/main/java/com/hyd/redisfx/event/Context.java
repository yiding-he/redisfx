package com.hyd.redisfx.event;

import com.hyd.fx.utils.Str;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 一个用于包装上下文的容器，加入泛型以便从中取任何类型的值。取值时只会作强制类型转换，因此可能会抛出 ClassCastException。
 *
 * @author Yiding
 */
@SuppressWarnings("unchecked")
public class Context {

    private Map<String, Object> map;

    public Context() {
        this.map = new HashMap<>();
    }

    public Context(Map<String, Object> map) {
        this.map = new HashMap<>(map);
    }

    public Context(Context c) {
        this.map = new HashMap<>(c.map);
    }

    public Context put(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

    public <E> E get(String key) {
        return (E) this.map.get(key);
    }

    public boolean containsKey(Object key) {
        return this.map.containsKey(key);
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public Set<String> keySet() {
        return this.map.keySet();
    }

    public Set<Map.Entry<String, Object>> entrySet() {
        return this.map.entrySet();
    }

    public Collection<Object> values() {
        return this.map.values();
    }

    public void remove(String key) {
        this.map.remove(key);
    }

    @Override
    public String toString() {
        return map.toString();
    }

    //////////////////////////////////////////////////////////////

    public int getIntValue(String key, int def) {
        if (!map.containsKey(key) || map.get(key) == null) {
            return def;
        }

        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        } else {
            try {
                return Integer.parseInt(String.valueOf(value));
            } catch (NumberFormatException e) {
                return def;
            }
        }
    }

    public double getDoubleValue(String key, double def) {
        if (!map.containsKey(key) || map.get(key) == null) {
            return def;
        }

        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else {
            try {
                return Double.parseDouble(String.valueOf(value));
            } catch (NumberFormatException e) {
                return def;
            }
        }
    }

    public long getLongValue(String key, long def) {
        if (!map.containsKey(key) || map.get(key) == null) {
            return def;
        }

        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        } else {
            try {
                return Long.parseLong(String.valueOf(value));
            } catch (NumberFormatException e) {
                return def;
            }
        }
    }

    public boolean getBooleanValue(String key, boolean def) {
        if (!map.containsKey(key) || map.get(key) == null) {
            return def;
        }

        Object value = map.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else {
            return Boolean.valueOf(String.valueOf(value));
        }
    }

    public int getIntValue(String key) {
        return getIntValue(key, 0);
    }

    public double getDoubleValue(String key) {
        return getDoubleValue(key, 0);
    }

    public long getLongValue(String key) {
        return getLongValue(key, 0);
    }

    public boolean getBooleanValue(String key) {
        return getBooleanValue(key, false);
    }

    public Integer getInteger(String key) {
        return (Integer) map.get(key);
    }

    public Double getDouble(String key) {
        return (Double) map.get(key);
    }

    public Long getLong(String key) {
        return (Long) map.get(key);
    }

    public Boolean getBoolean(String key) {
        return (Boolean) map.get(key);
    }

    public String getString(String key, String def) {
        if (!this.map.containsKey(key) || Str.isBlank((String)this.map.get(key))) {
            return def;
        } else {
            return (String) this.map.get(key);
        }
    }

    public String getString(String key) {
        return (String) this.map.get(key);
    }
}

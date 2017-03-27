package com.hyd.redisfx.event;

/**
 * 事件对象
 *
 * @author yiding_he
 */
public class Event<T> extends Context {

    private T eventType;    // 事件类型

    public Event(T eventType) {
        this.eventType = eventType;
    }

    public static Event of(String eventType) {
        return new Event<>(eventType);
    }

    public T getEventType() {
        return eventType;
    }

    @Override
    public Event<T> put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}

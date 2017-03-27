package com.hyd.redisfx.event;

/**
 * 事件总线相关异常
 *
 * @author yiding_he
 */
public class EventBusException extends RuntimeException {

    public EventBusException() {
    }

    public EventBusException(String message) {
        super(message);
    }

    public EventBusException(String message, Throwable cause) {
        super(message, cause);
    }

    public EventBusException(Throwable cause) {
        super(cause);
    }
}

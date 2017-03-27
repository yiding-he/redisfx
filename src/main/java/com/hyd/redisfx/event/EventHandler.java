package com.hyd.redisfx.event;

@FunctionalInterface
public interface EventHandler {

    void handle(Event event) throws Exception;
}

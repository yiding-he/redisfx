package com.hyd.redisfx.event;

@FunctionalInterface
public interface EventInitializer {

    void process(Event event) throws Exception;
}

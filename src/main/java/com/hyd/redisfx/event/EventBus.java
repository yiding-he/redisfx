package com.hyd.redisfx.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 事件总线
 * <p>
 * T: 事件分类对象的类型，建议用 enum 类型
 *
 * @author yiding_he
 */
public class EventBus<T> {

    private static final Logger LOG = LoggerFactory.getLogger(EventBus.class);

    private final Map<T, List<EventHandlerWrapper>> EVENT_MAP = new HashMap<>();

    private final Set<T> mutedEventTypes = new HashSet<>();

    /**
     * 添加侦听器，如果 handlerId 对应的侦听器已经存在，则用当前参数覆盖原有的侦听器。
     *
     * @param eventType    事件类型
     * @param handlerId    侦听器ID，将来用于解除侦听（必传）
     * @param eventHandler 侦听器（必传）
     */
    public void on(T eventType, String handlerId, EventHandler eventHandler) {

        if (eventHandler == null || handlerId == null) {
            return;
        }

        if (!EVENT_MAP.containsKey(eventType)) {
            EVENT_MAP.put(eventType, new ArrayList<>());
        }

        List<EventHandlerWrapper> handlers = EVENT_MAP.get(eventType);
        handlers.removeIf(handler -> Objects.equals(handler.getId(), handlerId));
        handlers.add(new EventHandlerWrapper(handlerId, eventHandler));

        LOG.debug("Event '" + eventType + "' registered with " + eventHandler);
    }

    /**
     * 添加侦听器
     *
     * @param eventType    事件类型
     * @param eventHandler 侦听器
     */
    public void on(T eventType, EventHandler eventHandler) {
        on(eventType, String.valueOf(eventHandler.hashCode()), eventHandler);
    }

    /**
     * 解除侦听器
     *
     * @param eventType    事件类型
     * @param eventHandler 要解除的侦听器
     */
    public void off(T eventType, EventHandler eventHandler) {
        List<EventHandlerWrapper> handlers = EVENT_MAP.get(eventType);
        if (handlers != null) {
            handlers.removeIf(h -> h.getEventHandler() == eventHandler);
        }
    }

    /**
     * 解除侦听器
     *
     * @param eventType      事件类型
     * @param eventHandlerId 要解除的侦听器ID，参考 {@link #on(T, String, EventHandler)} 的 handlerId 参数
     */
    public void off(T eventType, String eventHandlerId) {
        List<EventHandlerWrapper> handlers = EVENT_MAP.get(eventType);
        if (handlers != null) {
            handlers.removeIf(h -> Objects.equals(h.getId(), eventHandlerId));
        }
    }

    /**
     * 移除指定事件类型的所有侦听器
     *
     * @param eventType 事件类型
     */
    public void offAll(T eventType) {
        if (EVENT_MAP.containsKey(eventType)) {
            EVENT_MAP.remove(eventType);
        }
    }

    /**
     * 禁用指定的事件类型
     *
     * @param eventTypes 事件类型
     *
     * @return 后续操作（可选）
     */
    public MuteContext mute(T... eventTypes) {
        Collections.addAll(mutedEventTypes, eventTypes);
        return new MuteContext(eventTypes);
    }

    /**
     * 重新启用被禁用的事件类型
     *
     * @param eventTypes 事件类型
     */
    public void unmute(T... eventTypes) {
        for (T eventType : eventTypes) {
            mutedEventTypes.remove(eventType);
        }
    }

    public boolean isMuted(T eventType) {
        return mutedEventTypes.contains(eventType);
    }

    /**
     * 触发事件
     *
     * @param event 事件对象
     */
    public void post(Event<T> event) {
        T eventType = event.getEventType();

        if (isMuted(eventType)) {
            return;
        }

        if (EVENT_MAP.containsKey(eventType)) {
            for (EventHandlerWrapper eventHandlerWrapper : EVENT_MAP.get(eventType)) {
                try {
                    eventHandlerWrapper.getEventHandler().handle(event);
                } catch (Exception e) {
                    LOG.error("处理事件失败", e);
                }
            }
        }
    }

    /**
     * 触发事件（适用于没有属性的 Event 对象）
     *
     * @param eventType 事件类型
     */
    public void post(T eventType) {
        post(new Event<>(eventType));
    }

    /**
     * 触发事件（适用于 Event 对象仅有一个属性）
     *
     * @param eventType 事件类型
     * @param propName  Event 对象属性名
     * @param propValue Event 对象属性值
     */
    public void post(T eventType, String propName, Object propValue) {
        post(new Event<>(eventType).put(propName, propValue));
    }

    /**
     * 触发事件
     *
     * @param eventType        事件类型
     * @param eventInitializer 事件对象初始化
     *
     * @throws EventBusException 如果事件对象初始化出错
     */
    public void post(T eventType, EventInitializer eventInitializer) throws EventBusException {

        if (isMuted(eventType)) {
            return;
        }

        Event<T> event = new Event<>(eventType);
        if (eventInitializer != null) {
            try {
                eventInitializer.process(event);
            } catch (Exception e) {
                throw new EventBusException(e);
            }
        }
        post(event);
    }

    //////////////////////////////////////////////////////////////

    public class MuteContext {

        private T[] eventTypes;

        public MuteContext(T[] eventTypes) {
            this.eventTypes = eventTypes;
        }

        public void whenRunning(Runnable runnable) {
            try {
                runnable.run();
            } finally {
                for (T eventType : eventTypes) {
                    mutedEventTypes.remove(eventType);
                }
            }
        }
    }
}

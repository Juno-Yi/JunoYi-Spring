package com.junoyi.sdk.event;

/**
 * 事件抽象基类
 *
 * @author Fan
 */
public abstract class Event {

    private String name;
    private final boolean async;

    public Event() {
        this(false);
    }

    public Event(boolean isAsync) {
        this.async = isAsync;
    }

    public String getEventName() {
        if (this.name == null) {
            this.name = this.getClass().getSimpleName();
        }

        return this.name;
    }


    public final boolean isAsynchronous() {
        return this.async;
    }


}

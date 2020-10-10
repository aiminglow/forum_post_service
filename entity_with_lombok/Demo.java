package com.aiming.low.forum_post_service.entity;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(of = "new")
//@Value.Style(of = "new", allParameters = true)
public interface Demo {
    @Value.Parameter
    String userName();
    @Value.Parameter
    int userAge();
}

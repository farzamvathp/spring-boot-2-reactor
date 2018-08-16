package com.emersun.imi.collections;

public enum SubscribeType {
    SUBSCRIBE("subscribe"),
    UNSUBSCRIBE("unsubscribe"),
    RENEWAL("renewal"),
    FAILED_RENEWAL("failed_renewal");

    SubscribeType(String name) {
        this.name = name;
    }
    private String name;

    public String getName() {
        return name;
    }
}

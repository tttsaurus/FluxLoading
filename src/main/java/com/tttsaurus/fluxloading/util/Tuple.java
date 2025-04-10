package com.tttsaurus.fluxloading.util;

public class Tuple<T, J> {
    private final T first;
    private final J second;
    public Tuple(T object1, J object2) {
        this.first = object1;
        this.second = object2;
    }

    public T getFirst() {
        return first;
    }

    public J getSecond() {
        return second;
    }
}

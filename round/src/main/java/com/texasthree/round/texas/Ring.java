package com.texasthree.round.texas;

import java.util.function.Predicate;

public class Ring<T> {
    private Ring<T> next;
    private Ring<T> prev;

    T value;

    static <T> Ring<T> create(int num) {
        if (num < 0) {
            throw new IllegalArgumentException();
        }
        Ring<T> r = new Ring<>();
        for (int i = 1; i < num; i++) {
            r.link(null);
        }
        return r;
    }

    Ring() {
        next = this;
        prev = this;
    }

    void setValue(T v) {
        this.value = v;
    }

    void link(T v) {
        Ring<T> node = new Ring<>();
        node.value = v;

        node.next = this.next;
        node.prev = this;

        this.next.prev = node;
        this.next = node;
    }

    Ring<T> move(Predicate<T> filter) {
        int limit = this.size();
        Ring<T> n = this.next;
        while (limit > 0 && !filter.test(n.value)) {
            limit--;
            n = n.next;
        }
        return filter.test(n.value) ? n : null;
    }

    int size() {
        int size = 1;
        if (next == this) {
            return size;
        }

        Ring n = next;
        while (n != this) {
            size++;
            n = n.next;
        }
        return size;
    }

    Ring<T> getNext() {
        return next;
    }

    Ring<T> getPrev() {
        return prev;
    }
}

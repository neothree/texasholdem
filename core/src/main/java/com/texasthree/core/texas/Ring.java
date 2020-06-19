package com.texasthree.core.texas;

import java.util.function.Predicate;

public class Ring<T> {
    private Ring<T> next;
    private Ring<T> prev;

    public T value;

    public static <T> Ring<T> create(int num) {
        if (num < 0) {
            return null;
        }
        Ring<T> r = new Ring<>();
        for (int i = 1; i < num; i++) {
            r.link(null);
        }
        return r;
    }

    public Ring() {
        next = this;
        prev = this;
    }

    public void setValue(T v) {
        this.value = v;
    }

    public void link(T v) {
        Ring<T> node = new Ring<>();
        node.value = v;

        node.next = this.next;
        node.prev = this;

        this.next.prev = node;
        this.next = node;
    }


    public Ring move(int n) {
        Ring m = this;
        while (n > 0) {
            m = m.next;
            n--;
        }
        return m;
    }

    public Ring<T> move(Predicate<T> filter) {
        int limit = this.size();
        Ring<T> n = this.next;
        while (limit > 0 && !filter.test(n.value)) {
            limit--;
            n = n.next;
        }
        return filter.test(n.value) ? n : null;
    }

    public int size() {
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

    public Ring<T> getNext() {
        return next;
    }

    public Ring<T> getPrev() {
        return prev;
    }
}

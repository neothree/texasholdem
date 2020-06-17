package com.texasthree.core;

import java.util.function.Function;
import java.util.function.Predicate;

public class Ring<T> {
    private Ring<T> next;
    private Ring<T> prev;

    private T value;

//    public Ring() {
//        next = this;
//        prev = this;
//    }

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

    public Ring move(Predicate<T> filter) {
        int limit = this.size();
        Ring<T> n = this;
        while (limit > 0 && !filter.test(n.getValue())) {
            limit--;
            n = n.next;
        }
        return filter.test(n.value) ? n : null;
    }

    public void setValue(T v) {
        this.value = v;
    }

    public T getValue() {
        return value;
    }
//    public void unlink(int n) {
//        n = n % this.size();
//        if (n == 0) {
//            return;
//        }
//
//
//    }

    public int size() {
        int size = 1;
        if (next == this) {
            return size;
        }

        Ring n = next;
        while (n != next) {
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

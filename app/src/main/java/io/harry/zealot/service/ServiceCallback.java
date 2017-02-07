package io.harry.zealot.service;

public interface ServiceCallback<T> {
    void onSuccess(T result);
}

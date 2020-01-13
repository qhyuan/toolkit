package com.qyuan.toolkit.patterns;

/**
 * Created by qyuan on 2019-12-26.
 */
public class Singleton {

    private Singleton() {
    }

    public Singleton getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final Singleton INSTANCE = new Singleton();
    }
}

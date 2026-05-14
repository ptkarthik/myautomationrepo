package org.billing.lock;

public class BuildSynchronization {
    public static final Object BUILD_LOCK = new Object();
    public static boolean buildCompleted = false;
}
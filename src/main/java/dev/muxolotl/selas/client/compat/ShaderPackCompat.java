package dev.muxolotl.selas.client.compat;

import dev.muxolotl.selas.Selas;

import java.lang.reflect.Method;

public final class ShaderPackCompat {
    private static final long QUERY_INTERVAL_NANOS = 100_000_000L;

    private static Boolean irisApiPresent;
    private static Method getInstanceMethod;
    private static Method isShaderPackInUseMethod;

    private static long lastQueryNanos;
    private static boolean cachedShaderPackInUse;
    private static boolean loggedPresence;
    private static boolean loggedQueryFailure;

    private ShaderPackCompat() {
    }

    public static boolean isShaderPackInUse() {
        if (irisApiPresent == null) {
            resolveIrisApi();
        }

        if (!Boolean.TRUE.equals(irisApiPresent)) {
            return false;
        }

        long now = System.nanoTime();
        if (lastQueryNanos != 0L && now - lastQueryNanos < QUERY_INTERVAL_NANOS) {
            return cachedShaderPackInUse;
        }

        lastQueryNanos = now;
        try {
            Object api = getInstanceMethod.invoke(null);
            Object result = isShaderPackInUseMethod.invoke(api);
            cachedShaderPackInUse = result instanceof Boolean && (Boolean) result;
            loggedQueryFailure = false;
        } catch (ReflectiveOperationException | ClassCastException | LinkageError | SecurityException exception) {
            cachedShaderPackInUse = false;
            if (!loggedQueryFailure) {
                Selas.LOGGER.warn("Could not query Iris shader pack status; Selas will remain active", exception);
                loggedQueryFailure = true;
            }
        }

        return cachedShaderPackInUse;
    }

    private static void resolveIrisApi() {
        try {
            Class<?> irisApi = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
            getInstanceMethod = irisApi.getMethod("getInstance");
            isShaderPackInUseMethod = irisApi.getMethod("isShaderPackInUse");
            irisApiPresent = true;
            if (!loggedPresence) {
                Selas.LOGGER.info("Iris API detected; Selas can auto-disable while a shader pack is active.");
                loggedPresence = true;
            }
        } catch (ClassNotFoundException | NoSuchMethodException | LinkageError | SecurityException exception) {
            irisApiPresent = false;
            getInstanceMethod = null;
            isShaderPackInUseMethod = null;
            Selas.LOGGER.debug("Iris API not present or unavailable; shader auto-disable will stay inactive.", exception);
        }
    }
}

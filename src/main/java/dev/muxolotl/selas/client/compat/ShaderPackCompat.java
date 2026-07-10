package dev.muxolotl.selas.client.compat;

import dev.muxolotl.selas.Selas;

import java.lang.reflect.Method;

public final class ShaderPackCompat {
    private static Boolean irisApiPresent;
    private static Method getInstanceMethod;
    private static Method isShaderPackInUseMethod;
    private static boolean loggedPresence;

    private ShaderPackCompat() {
    }

    public static boolean isShaderPackInUse() {
        if (irisApiPresent == null) {
            resolveIrisApi();
        }

        if (!irisApiPresent) {
            return false;
        }

        try {
            Object api = getInstanceMethod.invoke(null);
            Object result = isShaderPackInUseMethod.invoke(api);
            return result instanceof Boolean && (Boolean) result;
        } catch (ReflectiveOperationException | ClassCastException exception) {
            Selas.LOGGER.debug("Could not query Iris shader pack status", exception);
            return false;
        }
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
        } catch (ClassNotFoundException | NoSuchMethodException exception) {
            irisApiPresent = false;
            getInstanceMethod = null;
            isShaderPackInUseMethod = null;
            Selas.LOGGER.debug("Iris API not present; shader auto-disable will stay inactive.");
        }
    }
}

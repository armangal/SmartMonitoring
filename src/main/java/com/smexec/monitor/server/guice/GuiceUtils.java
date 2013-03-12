package com.smexec.monitor.server.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class GuiceUtils {

    /**
     * The Guice injector
     */
    private static Injector injector = null;

    /**
     * @return The injector used by the application
     */
    public static Injector getInjector() {
        return injector;
    }

    /**
     * Initializes Guice.
     */
    public static void init(Module... modules) {
        if (injector != null) {
            return;
        }

        injector = Guice.createInjector(modules);
    }
}

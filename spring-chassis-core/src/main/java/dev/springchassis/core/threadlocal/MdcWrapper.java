package dev.springchassis.core.threadlocal;

import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;

import java.util.Map;

@RequiredArgsConstructor
public class MdcWrapper {

    private final Map<String, String> contextMap;

    public static MdcWrapper get() {
        return new MdcWrapper(MDC.getCopyOfContextMap());
    }

    public static void set(MdcWrapper obj) {
        if (obj != null && obj.contextMap != null) {
            MDC.setContextMap(obj.contextMap);
        }
    }

    public static void clear() {
        MDC.clear();
    }
}

package io.logz.benchmarks.elasticsearch.metrics;

import com.udojava.jmx.wrapper.JMXBean;
import com.udojava.jmx.wrapper.JMXBeanAttribute;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by roiravhon on 9/20/16.
 */
@JMXBean(description = "General JMX metrics")
public class GeneralMbean {

    private static GeneralMbean instance;
    private final AtomicInteger currStep;

    public static GeneralMbean getInstance() {
        if (instance == null) {
            instance = new GeneralMbean();
        }

        return instance;
    }

    private GeneralMbean() {
        currStep = new AtomicInteger(1);
    }

    @SuppressWarnings("unused")
    @JMXBeanAttribute(name = "currStep", description = "The current step running")
    public long getCurrStep() {
        return currStep.get();
    }

    public void incrementStep() {
        currStep.incrementAndGet();
    }
}
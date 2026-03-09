package com.cognivex.ai.service;

import jakarta.annotation.PostConstruct;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Service
public class MetricsService {

    private static final Logger logger = LoggerFactory.getLogger(MetricsService.class);
    private final CopyOnWriteArrayList<Consumer<Map<String, Object>>> subscribers = new CopyOnWriteArrayList<>();
    
    private long pid;
    private Map<String, Object> currentMetrics;

    @PostConstruct
    public void init() {
        // Get the process ID
        String name = ManagementFactory.getRuntimeMXBean().getName();
        pid = Long.parseLong(name.split("@")[0]);
        logger.info("MetricsService initialized with PID: {}", pid);
    }

    @Scheduled(fixedRate = 5000)
    public void collectMetrics() {
        try {
            Map<String, Object> metrics = new HashMap<>();
            
            // Timestamp
            metrics.put("timestamp", Instant.now().toString());
            metrics.put("pid", pid);
            
            // Memory metrics
            Map<String, Object> memory = getStringObjectMap();
            metrics.put("memory", memory);
            
            // Thread metrics
            ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
            Map<String, Object> threads = new HashMap<>();
            threads.put("count", threadBean.getThreadCount());
            threads.put("peak", threadBean.getPeakThreadCount());
            threads.put("daemon", threadBean.getDaemonThreadCount());
            threads.put("totalStarted", threadBean.getTotalStartedThreadCount());
            metrics.put("threads", threads);
            
            // Runtime metrics
            Map<String, Object> runtime = new HashMap<>();
            runtime.put("uptime", ManagementFactory.getRuntimeMXBean().getUptime());
            runtime.put("startTime", ManagementFactory.getRuntimeMXBean().getStartTime());
            runtime.put("vmName", ManagementFactory.getRuntimeMXBean().getVmName());
            runtime.put("vmVendor", ManagementFactory.getRuntimeMXBean().getVmVendor());
            runtime.put("vmVersion", ManagementFactory.getRuntimeMXBean().getVmVersion());
            metrics.put("runtime", runtime);
            
            // GC metrics
            Map<String, Object> gc = new HashMap<>();
            ManagementFactory.getGarbageCollectorMXBeans().forEach(gcBean -> {
                Map<String, Object> gcInfo = new HashMap<>();
                gcInfo.put("name", gcBean.getName());
                gcInfo.put("count", gcBean.getCollectionCount());
                gcInfo.put("time", gcBean.getCollectionTime());
                gcInfo.put("memoryPools", gcBean.getMemoryPoolNames());
                gc.put(gcBean.getName(), gcInfo);
            });
            metrics.put("gc", gc);
            currentMetrics = metrics;
            // Notify subscribers
            for (Consumer<Map<String, Object>> subscriber : subscribers) {
                try {
                    subscriber.accept(metrics);
                } catch (Exception e) {
                    logger.error("Error notifying subscriber: {}", e.getMessage());
                }
            }
            
            logger.debug("Metrics collected: {}", metrics.keySet());
            
        } catch (Exception e) {
            logger.error("Error collecting metrics: {}", e.getMessage(), e);
        }
    }

    private static @NonNull Map<String, Object> getStringObjectMap() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapUsage = memoryBean.getNonHeapMemoryUsage();

        Map<String, Object> memory = new HashMap<>();
        memory.put("heapInit", heapUsage.getInit());
        memory.put("heapUsed", heapUsage.getUsed());
        memory.put("heapMax", heapUsage.getMax());
        memory.put("heapCommitted", heapUsage.getCommitted());
        memory.put("nonHeapInit", nonHeapUsage.getInit());
        memory.put("nonHeapUsed", nonHeapUsage.getUsed());
        memory.put("nonHeapMax", nonHeapUsage.getMax());
        memory.put("nonHeapCommitted", nonHeapUsage.getCommitted());
        return memory;
    }

    public void subscribe(Consumer<Map<String, Object>> callback) {
        subscribers.add(callback);
        // Immediately send current metrics to new subscriber
        if (currentMetrics != null) {
            callback.accept(currentMetrics);
        }
    }
}

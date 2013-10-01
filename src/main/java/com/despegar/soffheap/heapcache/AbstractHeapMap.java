package com.despegar.soffheap.heapcache;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.despegar.soffheap.metrics.JMetrics;

import static java.text.MessageFormat.format;

public abstract class AbstractHeapMap<Key,Value> implements HeapCache<Key,Value>{
    protected final MetricRegistry registry;
	protected final Counter hitsCounter;
    protected final Counter missesCounter;
    protected final Counter invalidatesCounter;

	
	public AbstractHeapMap(String name) {
        registry = JMetrics.getMetrics();
        hitsCounter = registry.counter(format("{0}.{1}hits", HeapCache.class.getName(), name));
        missesCounter = registry.counter(format("{0}.{1}misses", HeapCache.class.getName(), name));
        invalidatesCounter = registry.counter(format("{0}.{1}invalidates", HeapCache.class.getName(), name));

        String hitratio = format("{0}.{1}hitRatio", HeapCache.class.getName(), name);
		if (registry.getGauges().get(hitratio) != null) {
			registry.register(hitratio,  new Gauge<Double>() {
				@Override
				public Double getValue() {
					double hits = hitsCounter.getCount();
					double attemps = hits + missesCounter.getCount();
					if (attemps == 0d) return 0d;
					return (hits / attemps) * 100;
				}

			});
		}
    }
}

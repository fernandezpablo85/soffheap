package com.despegar.soffheap.heapcache;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.despegar.soffheap.metrics.JMetrics;

public abstract class AbstractHeapMap<Key,Value> implements HeapCache<Key,Value>{

	protected MetricRegistry registry = JMetrics.getMetrics();
	protected Counter hitsCounter = registry.counter(HeapCache.class.getName()+".hits");
	protected Counter missesCounter = registry.counter(HeapCache.class.getName()+".misses");
	protected Counter invalidatesCounter = registry.counter(HeapCache.class.getName()+".invalidates");

	
	public AbstractHeapMap() {
		String hitratio = HeapCache.class.getName()+".hitRatio";
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

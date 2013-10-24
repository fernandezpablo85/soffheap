package com.despegar.soffheap;

import com.despegar.soffheap.snapshot.DataSource;

import java.util.HashMap;
import java.util.Map;

public class SomeDataSource implements DataSource<String, PojoValue> {
    @Override
    public Map<String, PojoValue> get() {
        HashMap<String, PojoValue> ds = new HashMap<>();
        ds.put("Test",new PojoValue("Test", 1l));
        return ds;
    }
}

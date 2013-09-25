package com.despegar.offheap;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.esotericsoftware.kryo.Kryo;

public class KryoPool
{
    private final Queue<Kryo> objects = new ConcurrentLinkedQueue<Kryo>();

    public Kryo get()
    {
    	Kryo kh;
        if ((kh = objects.poll()) == null)
        {
            kh = createInstance();
        }
        return kh;
    }

    public void done(Kryo kh)
    {
        objects.offer(kh);
    }

    public void close()
    {
        objects.clear();
    }

    /**
     * Sub classes can customize the Kryo instance by overriding this method
     *
     * @return create Kryo instance
     */
    protected Kryo createInstance()
    {
        Kryo kryo = new Kryo();
        kryo.setReferences(false);
        return kryo;
    }

}

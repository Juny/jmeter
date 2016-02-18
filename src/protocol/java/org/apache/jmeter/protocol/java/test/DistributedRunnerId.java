package org.apache.jmeter.protocol.java.test;

import java.util.concurrent.atomic.AtomicInteger;

public class DistributedRunnerId {

    private static AtomicInteger id = new AtomicInteger(-1);
    
    public static int getRunnerId(){
        return id.incrementAndGet();
    }
    
    public static void main(String[] arg){
        System.out.println(getRunnerId());
    }

}

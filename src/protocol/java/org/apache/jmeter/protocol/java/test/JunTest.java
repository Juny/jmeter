package org.apache.jmeter.protocol.java.test;

import java.io.Serializable;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleListener;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

public class JunTest extends AbstractJavaSamplerClient implements Serializable {

    private static final long serialVersionUID = 240L;
    private static final Logger log = LoggingManager.getLoggerForClass();
    
    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        log.info("begin runTest.");
//        SampleResult results = new SampleResult();
//        results.setResponseCode("OK");
//        results.setResponseMessage("OK Message");
//        results.setSampleLabel("JunTest" + new Random().nextInt(10));
//        results.setSamplerData("Jun SampleData");
//        results.setResponseData("Jun ResultData", null);
//        results.setDataType(SampleResult.TEXT);
//        results.sampleStart();
//
//        try {
//            TimeUnit.MILLISECONDS.sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        results.setSuccessful(true);
//        results.sampleEnd();
//        return results;
        
        SampleResult action = new SampleResult();
        action.setSampleLabel("Action");
        action.sampleStart();
      
        // 另外起一个线程显示事务，在一个线程来的话停止时容易报错，提示不能停止
        ShowTrans showTrans = new ShowTrans(context);
        showTrans.start();
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // 不加这个点击停止时会出现无法停止的异常
                break;
            }
        }
        showTrans.stop();
        action.sampleEnd();
        
        log.info("end runTest.");
        return action;
    }


    class ShowTrans extends Thread {
        JavaSamplerContext context;
        Random r = new Random();

        ShowTrans(JavaSamplerContext context) {
            this.context = context;
            this.setName("ShowTransThread");
        }

        public void run() {
            log.info("begin show trans.");
            while (true) {
                SampleResult result = new SampleResult();
                result.setResponseCode("OK");
                result.setResponseMessage("OK Message");
                result.setSampleLabel("JunTest" + new Random().nextInt(10));
                result.setSamplerData("Jun SampleData");
                result.setResponseData("Jun ResultData", null);
                result.setDataType(SampleResult.TEXT);
                result.setSuccessful(true);
                long start = new Date().getTime();
                result.setElapsedTime(start, start + r.nextInt(100));
                
                SampleEvent event = new SampleEvent(result, "threadGroupName", new JMeterVariables());
                log.info("context:" + context);
                log.info("context.sampleListeners:" + context.sampleListeners.size());
                for (SampleListener sampleListener : context.sampleListeners) {
                    sampleListener.sampleOccurred(event);
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // 不加这个点击停止时会出现无法停止的异常
                    break;
                }
            }
        }
    }

}
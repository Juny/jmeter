/**
 * 
 */
package org.apache.jmeter.protocol.java.test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleListener;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import kraken.common.Transaction;
import kraken.common.TransactionManager;

/**
 * @author GuoJunYing
 *
 */
public class ShowTransactionThread extends Thread {
    private static final long serialVersionUID = 240L;
    private static final Logger log = LoggingManager.getLoggerForClass();
    
    private JavaSamplerContext context;
    private Object transManager;
    private Random r = new Random();
    public boolean started = true;
    private Class<?> classTransaction;
    private Method getReasonMethod;
    private Method getNameMethod;
    private Method getEndStatusMethod;
    private Method getStartTimeMethod;
    private Method getDurationMethod;

    ShowTransactionThread(JavaSamplerContext context, Object transManager) {
        this.context = context;
        this.transManager = transManager;
        this.setName("ShowTransThread");
        
        try {
            classTransaction = Thread.currentThread().getContextClassLoader().loadClass("kraken.common.Transaction");
            getReasonMethod = classTransaction.getMethod("getReason");
            getNameMethod = classTransaction.getMethod("getName");
            getEndStatusMethod = classTransaction.getMethod("getEndStatus");
            getStartTimeMethod = classTransaction.getMethod("getStartTime");
            getDurationMethod = classTransaction.getMethod("getDuration");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
    
    public void setTransManager(Object transManager) {
        this.transManager = transManager;
        log.info("setTransManager. " + transManager);
    }
    
    public void run() {
        try{
            log.info("begin show trans.");
            while (started) {
                if(transManager == null){
                    log.info("transManager == null.");
                    try {
                        Thread.sleep(1000);
                        continue;
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                    
                Class<?> classTransactionManager = Thread.currentThread().getContextClassLoader().loadClass("kraken.common.TransactionManager");
                Method getTransactionListMethod = classTransactionManager.getMethod("getTransactionList"); 
                log.info("getTransactionListMethod is null : " + (getTransactionListMethod == null));
                log.info("transManager is null : " + (transManager == null));
                List<?> transList = (List<?>) getTransactionListMethod.invoke(transManager); 
                
                //ArrayList<Transaction> transList = this.transManager.getTransactionList();
                for(Object t : transList){
                    SampleResult result = new SampleResult();
                    result.setResponseCode((String) getReasonMethod.invoke(t));//t.getReason());
                    result.setResponseMessage((String) getReasonMethod.invoke(t));//t.getReason());
                    result.setSampleLabel((String) getNameMethod.invoke(t));//t.getName());
                    result.setSamplerData("TODO...001");
                    result.setResponseData("TODO...002", null);
                    result.setDataType(SampleResult.TEXT);
                    result.setSuccessful(getEndStatusMethod.invoke(t).equals(0) ? true : false);
                    long start = ((Date) getStartTimeMethod.invoke(t)).getTime();
                    result.setElapsedTime(start, start + (long) ((float)getDurationMethod.invoke(t) * 1000));
                    
                    SampleEvent event = new SampleEvent(result, "threadGroupName", new JMeterVariables());
                    log.info("context:" + context);
                    log.info("context.sampleListeners:" + context.sampleListeners.size());
                    for (SampleListener sampleListener : context.sampleListeners) {
                        sampleListener.sampleOccurred(event);
                    }
                }
                
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.error("ShowTransactionThread. sleep error.",e);
                    // 不加这个点击停止时会出现无法停止的异常
                    break;
                }
            }
        }catch(Exception ex){
            System.out.println(ex);
            ex.printStackTrace();
        }
    }
}

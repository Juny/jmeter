/**
 * 
 */
package org.apache.jmeter.protocol.java.test;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.security.AccessController;
import kraken.common.Config;
import kraken.common.TransactionManager;
import kraken.common.UserManager;

import org.apache.jmeter.DynamicClassLoader;
import org.apache.jmeter.NewDriver;
//import org.apache.jmeter.engine.DistributedRunnerIdFactory;
//import org.apache.jmeter.engine.DistributedRunnerIdFactoryImpl;
import org.apache.jmeter.engine.RemoteJMeterEngine;
import org.apache.jmeter.engine.RemoteJMeterEngineImpl;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;


/**
 * @author jun
 *
 */
public class KrakenEntrance extends AbstractJavaSamplerClient implements Serializable {

    private static final long serialVersionUID = 240L;
    
    private static final String CLASSPATH_SEPARATOR = File.pathSeparator;

    private static final String OS_NAME = System.getProperty("os.name");// $NON-NLS-1$

    private static final String OS_NAME_LC = OS_NAME.toLowerCase(java.util.Locale.ENGLISH);

    private static final String JAVA_CLASS_PATH = "java.class.path";// $NON-NLS-1$

    private static final Logger log = LoggingManager.getLoggerForClass();
    
    /** The class loader to use for loading JMeter classes. */
    private static DynamicClassLoader loader;

//    private UserManager ua;
    private ShowTransactionThread showTrans;

    private Class<?> classKrakenNewDriver;

    private Class<?> classGo;

    private Class<?> classUserManager;

    private Class<?> classTransactionManager;
    
    /**
     * 
     */
    public KrakenEntrance() {
        // TODO Auto-generated constructor stub
    }
    
    /* Implements JavaSamplerClient.setupTest(JavaSamplerContext) */
    @Override
    public void setupTest(JavaSamplerContext context) {
        //log.debug(getClass().getName() + ": setupTest");
    }

    /* Implements JavaSamplerClient.teardownTest(JavaSamplerContext) */
    @Override
    public void teardownTest(JavaSamplerContext context) {
        //log.debug(getClass().getName() + ": teardownTest");
    }

    /* (non-Javadoc)
     * @see org.apache.jmeter.protocol.java.sampler.JavaSamplerClient#runTest(org.apache.jmeter.protocol.java.sampler.JavaSamplerContext)
     */
    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        System.setProperty("kraken.home", NewDriver.getJMeterDir() + "\\kraken");
        String kraken_home = System.getProperty("kraken.home");

        loader = AccessController.doPrivileged(
                new java.security.PrivilegedAction<DynamicClassLoader>() {
                    @Override
                    public DynamicClassLoader run() {
                        return new DynamicClassLoader(new URL[]{}, Thread.currentThread().getContextClassLoader());
                    }
                }
        );
        
        
        log.info("runTest start.");
        try {
            loader.addPath(kraken_home + "/config");
            loader.addPath(kraken_home + "/data");
            loader.addPath(kraken_home + "/lib");

            Thread.currentThread().setContextClassLoader(loader);
            classKrakenNewDriver = Thread.currentThread().getContextClassLoader().loadClass("kraken.common.NewDriver");
            Method method = classKrakenNewDriver.getMethod("setKrakenHome", String.class);  
            method.invoke(null, kraken_home);  

            showTrans = new ShowTransactionThread(context, null);
            
            classGo = Thread.currentThread().getContextClassLoader().loadClass("kraken.Go");
            Method startMethod = classGo.getMethod("start", int.class, Thread.class); 
            int id = RemoteJMeterEngineImpl.runnerId; //DistributedRunnerId.getRunnerId();
            log.info("runTest start. RunnerId:" + id);
            startMethod.invoke(null, id, showTrans);  
            
            classUserManager = Thread.currentThread().getContextClassLoader().loadClass("kraken.common.UserManager");
            Method getTransManager = classUserManager.getMethod("getTransManager");  
            showTrans.setTransManager(getTransManager.invoke(null));
            System.out.println("start ok.");

            while (true) {
                //TransactionManager.syncGrafanaDashboard();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    log.error("runTest. ShowTransactionThread sleep error.",e);
                    // 不加这个点击停止时会出现无法停止的异常
                    break;
                }
            }
        } catch (Throwable e) {
            log.error("runTest exception.",e);
            log.error("runTest exception. cause.",e.getCause());

            for(Throwable ex : e.getSuppressed()){
                log.error("runTest exception. xx.", ex);
            }
        }
        finally {
            if(showTrans != null){
                showTrans.started = false;
                showTrans.interrupt();
            }
            try {
                Method stopMethod = classGo.getMethod("stop");
                stopMethod.invoke(null);  
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                loader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            loader = null;
        }
        SampleResult sr = new SampleResult();
        sr.sampleStart();
        sr.setSuccessful(true);
        sr.sampleEnd();
        return sr;  
    }
}

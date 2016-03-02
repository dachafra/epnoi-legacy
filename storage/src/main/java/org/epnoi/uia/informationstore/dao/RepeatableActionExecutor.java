package org.epnoi.uia.informationstore.dao;

import com.hp.hpl.jena.shared.JenaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Created by cbadenes on 02/03/16.
 */
public class RepeatableActionExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(RepeatableActionExecutor.class);

    private static final Integer WAITING_TIME = 500; //msecs

    private static final Integer MAX_RETRIES = 5;


    public static void waitForRetry(Integer retries){
        try {

            Thread.sleep(Double.valueOf(Math.exp(retries)*WAITING_TIME).longValue());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static Optional<Object> performRetries(Integer retries, String id, RepeatableAction function){
        try {
            return Optional.of(function.run());
        }catch (JenaException e){
            if (retries > MAX_RETRIES){
                LOG.error("Error executing "+id+" after " + MAX_RETRIES + " retries",e);
                return Optional.empty();
            }
            else{
                LOG.info("Trying to retry "+id+": " + retries);
                waitForRetry(retries);
                return performRetries(++retries,id,function);
            }
        }catch (Exception e){
            LOG.error("Error on operation: " + id, e);
            return Optional.empty();
        }
    }
}
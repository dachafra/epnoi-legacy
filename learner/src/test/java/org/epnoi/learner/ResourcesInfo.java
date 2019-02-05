package org.epnoi.learner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.text.NumberFormat;

public class ResourcesInfo {

    private static final Logger LOG = LoggerFactory.getLogger(ResourcesInfo.class);

    //final long mb = 1024 * 1024;

    public long getMaxMemory(){
        return Runtime.getRuntime().maxMemory();
    }

    public long getAllocatedMemory(){
        return Runtime.getRuntime().totalMemory();
    }

    public long getFreeMemory(){
        return Runtime.getRuntime().freeMemory();
    }

    public long getCosumtionMemory(){
        return getMaxMemory() - getFreeMemory();
    }

    public long getTotalFreeMemory(){
        return getFreeMemory() + (getMaxMemory() - getAllocatedMemory());
    }

    public long getAvailableProcessors(){
        return Runtime.getRuntime().availableProcessors();
    }

    public void setup(PrintWriter pw){
        final NumberFormat format = NumberFormat.getInstance();
        final String mega = " B";
        LOG.info("========================== Resources Info ==========================");
        LOG.info("Available Processors: " + format.format(getAvailableProcessors()));
        LOG.info("Free memory: " + format.format(getFreeMemory()) + mega);
        LOG.info("Allocated memory: " + format.format(getAllocatedMemory()) + mega);
        LOG.info("Max memory: " + format.format(getMaxMemory()) + mega);
        LOG.info("Total free memory: " + format.format(getTotalFreeMemory()) + mega);
        LOG.info("=================================================================\n");
    }
}

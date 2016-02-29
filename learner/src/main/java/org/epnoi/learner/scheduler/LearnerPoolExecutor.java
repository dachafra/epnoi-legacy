package org.epnoi.learner.scheduler;

import org.epnoi.learner.helper.LearnerHelper;
import org.epnoi.model.domain.resources.Document;
import org.epnoi.model.domain.resources.Domain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;

/**
 * Created by cbadenes on 21/02/16.
 */
public class LearnerPoolExecutor {
    private static final Logger LOG = LoggerFactory.getLogger(LearnerPoolExecutor.class);

    private final long delay;
    private final LearnerHelper helper;
    private final Document document;

    private ThreadPoolTaskScheduler threadpool;
    private ScheduledFuture<?> task;


    public LearnerPoolExecutor(Document document, LearnerHelper helper, long delayInMsecs) {
        this.document = document;
        this.delay  = delayInMsecs;
        this.helper = helper;

        this.threadpool = new ThreadPoolTaskScheduler();
        this.threadpool.setPoolSize(1);
        this.threadpool.initialize();

        LOG.info("created a new modeling executor delayed by: " + delayInMsecs + "msecs for document: " + document);
    }

    public LearnerPoolExecutor buildModel(){
        LOG.info("scheduling a new build model task");
        if (task != null) task.cancel(false);
        this.task = this.threadpool.schedule(new LearnerTask(document,helper), new Date(System.currentTimeMillis() + delay));
        return this;
    }
}


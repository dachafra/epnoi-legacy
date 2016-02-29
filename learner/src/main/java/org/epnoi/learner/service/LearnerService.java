package org.epnoi.learner.service;

import org.epnoi.learner.helper.LearnerHelper;
import org.epnoi.model.domain.resources.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;

/**
 * Created by cbadenes on 21/02/16.
 */
@Component
public class LearnerService {

    private static final Logger LOG = LoggerFactory.getLogger(LearnerService.class);

    @Value("${learner.task.delay}")
    protected Long delay;

    @Autowired
    LearnerHelper helper;

    private ThreadPoolTaskScheduler threadpool;
    private ScheduledFuture<?> task;

    @PostConstruct
    public void setup(){
//        this.executors = new ConcurrentHashMap<>();
        this.threadpool = new ThreadPoolTaskScheduler();
        this.threadpool.setPoolSize(1);
        this.threadpool.initialize();
    }


    public void buildModels(Document document){
        LOG.info("Plan a new task to build a lexical model for document: " + document);
        //TODO Implement Multi-Domain
        if (task != null) task.cancel(false);
        this.task = this.threadpool.schedule(new LearnerTask(document,helper), new Date(System.currentTimeMillis() + delay));
    }

}

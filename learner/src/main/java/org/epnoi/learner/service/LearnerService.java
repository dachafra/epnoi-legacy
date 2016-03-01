package org.epnoi.learner.service;

import org.epnoi.learner.helper.LearnerHelper;
import org.epnoi.model.domain.relations.Relation;
import org.epnoi.model.domain.resources.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * Created by cbadenes on 21/02/16.
 */
@Component
public class LearnerService {

    private static final Logger LOG = LoggerFactory.getLogger(LearnerService.class);

    private ConcurrentHashMap<String,ScheduledFuture<?>> tasks;

    @Value("${learner.task.delay}")
    protected Long delay;

    @Autowired
    LearnerHelper helper;

    private ThreadPoolTaskScheduler threadpool;


    @PostConstruct
    public void setup(){
        this.tasks = new ConcurrentHashMap<>();

        this.threadpool = new ThreadPoolTaskScheduler();
        this.threadpool.setPoolSize(10);
        this.threadpool.initialize();
    }


    public void buildModels(Relation relation){
        String domainUri = relation.getStartUri();

        LOG.info("Planning a new task to build a lexical model for domain: " + domainUri);

        ScheduledFuture<?> task = tasks.get(domainUri);
        if (task != null) task.cancel(false);
        task = this.threadpool.schedule(new LearnerTask(domainUri,helper), new Date(System.currentTimeMillis() + delay));
        tasks.put(domainUri,task);
    }

}

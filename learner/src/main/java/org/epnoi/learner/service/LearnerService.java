package org.epnoi.learner.service;

import org.epnoi.learner.helper.LearnerHelper;
import org.epnoi.learner.scheduler.LearnerPoolExecutor;
import org.epnoi.model.domain.resources.Document;
import org.epnoi.model.domain.resources.Domain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by cbadenes on 21/02/16.
 */
@Component
public class LearnerService {

    private static final Logger LOG = LoggerFactory.getLogger(LearnerService.class);

    private ConcurrentHashMap<String,LearnerPoolExecutor> executors;

    @Value("${learner.task.delay}")
    protected Long delay;

    @Autowired
    LearnerHelper helper;

    @PostConstruct
    public void setup(){
        this.executors = new ConcurrentHashMap<>();
    }


    public void buildModels(Document document){
        LOG.info("Plan a new task to build a lexical model for document: " + document);
        LearnerPoolExecutor executor = executors.get(document.getUri());
        if (executor == null){
            executor = new LearnerPoolExecutor(document,helper,delay);
        }
        executors.put(document.getUri(),executor.buildModel());
    }

}

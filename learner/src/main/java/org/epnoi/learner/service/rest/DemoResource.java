package org.epnoi.learner.service.rest;

import org.epnoi.learner.filesystem.DemoDataLoader;
import org.epnoi.learner.modules.Learner;
import org.epnoi.model.modules.Core;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;


@Component
public class DemoResource {
    private static final Logger logger = Logger.getLogger(DemoResource.class
            .getName());

    @Autowired
    private DemoDataLoader demoDataLoader;


    @PostConstruct
    public void init() {
        logger.info("Starting the " + this.getClass());
    }


    public void create() {
        demoDataLoader.load();
    }

    public void remove() {
        demoDataLoader.erase();
    }

}
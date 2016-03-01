package org.epnoi.learner.eventbus;

import org.epnoi.learner.service.LearnerService;
import org.epnoi.model.Event;
import org.epnoi.model.domain.relations.Relation;
import org.epnoi.model.domain.resources.Document;
import org.epnoi.model.domain.resources.Domain;
import org.epnoi.model.domain.resources.Resource;
import org.epnoi.model.modules.RoutingKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by cbadenes on 21/02/16.
 */
@Component
public class DocumentCreateddEventHandler extends AbstractEventHandler {

        private static final Logger LOG = LoggerFactory.getLogger(DocumentCreateddEventHandler.class);

        @Autowired
        LearnerService service;

        public DocumentCreateddEventHandler() {
            super(RoutingKey.of(Relation.Type.CONTAINS, Relation.State.CREATED));
        }

        @Override
        public void handle(Event event) {
            LOG.info("Domain updated event received: " + event);
            try{
                Relation relation = event.to(Relation.class);

                service.buildModels(relation);

            } catch (Exception e){
                // TODO Notify to event-bus when source has not been added
                LOG.error("Error scheduling a new topic model from domain: " + event, e);
            }
        }
}
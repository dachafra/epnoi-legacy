package org.epnoi.learner.service.rest;

import org.epnoi.learner.filesystem.FilesystemHarvester;
import org.epnoi.learner.modules.Learner;
import org.epnoi.learner.relations.corpus.RelationalSentencesCorpusCreationParameters;
import org.epnoi.model.Relation;
import org.epnoi.model.Term;
import org.epnoi.model.modules.Core;
import org.epnoi.model.rdf.RDFHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


@Component
public class LearnerResource {
    private static final Logger logger = LoggerFactory.getLogger(LearnerResource.class);

    @Autowired
    private Learner learner;

    @Autowired
    private Core core;

    @Autowired
    private FilesystemHarvester harvester;


    @PostConstruct
    public void init() {
        logger.info("Starting the " + this.getClass());
    }


    public List<Relation> getRelations(String id) {
        String uri = id;
        if (!id.startsWith("http")){
            uri = "http://"+id;
        }
        List<Relation> relations = new ArrayList<>();

        if (this.core.getInformationHandler().contains(uri, RDFHelper.DOMAIN_CLASS)) {
            relations = new ArrayList<>(learner.retrieveRelations(uri).getRelations());
        }else{
            logger.warn("Domain not exist in Information Handler: " + uri);
        }

        return relations;
    }

    public List<Term> getTerms(String id) {
        String uri = id;
        if (!id.startsWith("http")){
            uri = "http://"+id;
        }
        List<Term> terms = new ArrayList<>();
        logger.info("Extracting the terminology for the domain " + uri);
        if (this.core.getInformationHandler().contains(uri, RDFHelper.DOMAIN_CLASS)) {
            logger.info("Retrieving the terms since the domain is in stored in the uia");
            terms = new ArrayList<>(learner.retrieveTerminology(uri).getTerms());
        }else{
            logger.warn("Domain not exist in Information Handler: " + uri);
        }
        return terms;
    }

    public URI learnDomain(String id) {
        String uri = id;
        if (!id.startsWith("http")){
            uri = "http://"+id;
        }
        URI domainUri= null;
        try {
            learner.learn(uri);
            domainUri =
                    UriBuilder.fromUri((String) learner.getTrainer().getRelationalSentencesCorpusCreationParameters().getParameterValue(RelationalSentencesCorpusCreationParameters.RELATIONAL_SENTENCES_CORPUS_URI)).build();

        } catch (Exception e) {
            logger.error("Error during learning process",e);
        }
        return domainUri;
    }

}
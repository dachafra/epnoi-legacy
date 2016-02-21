package org.epnoi.learner.service.rest;

import org.epnoi.learner.modules.Learner;
import org.epnoi.learner.modules.Trainer;
import org.epnoi.learner.relations.corpus.RelationalSentencesCorpusCreationParameters;
import org.epnoi.learner.relations.patterns.RelationalPatternsModelCreationParameters;
import org.epnoi.model.commons.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Component
public class TrainerResource {
    private static final Logger logger = Logger.getLogger(TrainerResource.class
            .getName());

    @Autowired
    private Learner learner;


    @PostConstruct
    public void init() {
        logger.info("Starting the " + this.getClass());
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})

    public Response getConfiguration() {
        Map<String, Object> trainerConfiguration = new HashMap<String, Object>();
        trainerConfiguration.put("relationalPatternsModelCreationParameters", learner.getTrainer().getRelationalPatternsModelCreationParameters());
        trainerConfiguration.put("relationalSentencesCorpusCreationParamaters", learner.getTrainer().getRelationalSentencesCorpusCreationParameters());
        return Response.status(Response.Status.OK).entity(trainerConfiguration).build();
    }

    public URI createRelationalSentenceCorpus( int textCorpusMaxSize,String id)
    {

        String uri = id;
        if (!id.startsWith("http")){
            uri = "http://"+id;
        }
        Parameters<Object> runtimeParameters = new Parameters<Object>();

        runtimeParameters.setParameter(RelationalSentencesCorpusCreationParameters.RELATIONAL_SENTENCES_CORPUS_URI, uri);
        runtimeParameters.setParameter(RelationalSentencesCorpusCreationParameters.MAX_TEXT_CORPUS_SIZE, textCorpusMaxSize);


        learner.getTrainer().createRelationalSentencesCorpus(runtimeParameters);
        URI createdResourceUri = null;
        if (runtimeParameters.getParameterValue(RelationalSentencesCorpusCreationParameters.RELATIONAL_SENTENCES_CORPUS_URI) != null) {

            Trainer trainer = learner.getTrainer();

            Parameters<Object> runParam = trainer.getRuntimeParameters();

            String relSentenceCorpusUri = (String) runParam.getParameterValue(RelationalSentencesCorpusCreationParameters.RELATIONAL_SENTENCES_CORPUS_URI);

            createdResourceUri =
                    UriBuilder.fromUri(relSentenceCorpusUri).build();
        } else {
            createdResourceUri =
                    UriBuilder.fromUri((String) learner.getTrainer().getRelationalSentencesCorpusCreationParameters()
                            .getParameterValue(RelationalSentencesCorpusCreationParameters.RELATIONAL_SENTENCES_CORPUS_URI)).build();

        }
        return createdResourceUri;
    }



    // -----------------------------------------------------------------------------------------

    public URI createRelationalPatternsModel(String modelpath) {
        Parameters<Object> runtimeParameters = new Parameters<Object>();
        runtimeParameters.setParameter(RelationalPatternsModelCreationParameters.MODEL_PATH, modelpath);
        learner.getTrainer().createRelationalPatternsModel(runtimeParameters);

        URI uri =
                UriBuilder.fromUri((String) learner.getTrainer().getRelationalSentencesCorpusCreationParameters().getParameterValue(RelationalSentencesCorpusCreationParameters.RELATIONAL_SENTENCES_CORPUS_URI)).build();
        return uri;
    }

    public Response createDemoData() {

/*

        Parameters<Object> runtimeParameters = new Parameters<Object>();

        runtimeParameters.setParameter(RelationalSentencesCorpusCreationParameters.RELATIONAL_SENTENCES_CORPUS_URI, uri);
        runtimeParameters.setParameter(RelationalSentencesCorpusCreationParameters.MAX_TEXT_CORPUS_SIZE, textCorpusMaxSize);


        learner.getTrainer().createRelationalSentencesCorpus(runtimeParameters);
        URI createdResourceUri = null;
        if (runtimeParameters.getParameterValue(RelationalSentencesCorpusCreationParameters.RELATIONAL_SENTENCES_CORPUS_URI) != null) {

            createdResourceUri =
                    UriBuilder.fromUri((String) learner.getTrainer().getRuntimeParameters()
                            .getParameterValue(RelationalSentencesCorpusCreationParameters.RELATIONAL_SENTENCES_CORPUS_URI)).build();
        } else {
            createdResourceUri =
                    UriBuilder.fromUri((String) learner.getTrainer().getRelationalSentencesCorpusCreationParameters()
                            .getParameterValue(RelationalSentencesCorpusCreationParameters.RELATIONAL_SENTENCES_CORPUS_URI)).build();

        }
        return Response.created(createdResourceUri).build();
    */
      return  Response.ok().build();
    }
}
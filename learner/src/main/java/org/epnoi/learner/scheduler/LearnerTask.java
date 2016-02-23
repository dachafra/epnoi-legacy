package org.epnoi.learner.scheduler;

import org.epnoi.learner.helper.LearnerHelper;
import org.epnoi.learner.relations.corpus.RelationalSentencesCorpusCreationParameters;
import org.epnoi.learner.relations.patterns.RelationalPatternsModelCreationParameters;
import org.epnoi.model.Term;
import org.epnoi.model.commons.Parameters;
import org.epnoi.model.domain.relations.AppearedIn;
import org.epnoi.model.domain.relations.HypernymOf;
import org.epnoi.model.domain.relations.MentionsFromTerm;
import org.epnoi.model.domain.relations.Relation;
import org.epnoi.model.domain.resources.Domain;
import org.epnoi.model.domain.resources.Resource;
import org.epnoi.model.domain.resources.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by cbadenes on 21/02/16.
 */
public class LearnerTask implements Runnable{

    private static final Logger LOG = LoggerFactory.getLogger(LearnerTask.class);

    protected final Domain domain;
    protected final LearnerHelper helper;

    public LearnerTask(Domain domain, LearnerHelper helper){
        this.domain = domain;
        this.helper = helper;
    }


    @Override
    public void run() {

        try{

            String domainUri = "http://" + domain.getName();

            // Load papers from domain
            loadData(domainUri);

            // Train the learner from wikipedia pages
            //train();

            // Learn terms and relations from domain
            learn(domainUri);

        }catch (Exception e){
            LOG.warn("Unexpected error trying to learn terms and relations from domain: " + domain);
        }
    }


    private void loadData(String uri){
        //TODO use directly domain.uri
//            LOG.info("Removing previously analized  data ..");
//            helper.getDemoDataLoader().erase();

        //TODO use directly domain.uri
        LOG.info("Loading papers from domain: " + domain + " ..");
        helper.getDemoDataLoader().load();
    }


    private void train(){
        //TODO use directly domain.uri
        LOG.info("Creating relational sentences corpus ..");
        URI relURI = createRelationalSentencesCorpus(helper.getMaxLength());
        LOG.info("Relational sentences corpus created successfully: " + relURI);

        LOG.info("Creating relational pattern model ..");
        URI patUri = createRelationalPatternModel(helper.getModelPath());
        LOG.info("Relational pattern model created successfully: " + patUri);
    }

    private void learn(String uri){
        LOG.info("Learning terms and relations from domain: " + domain + " ..");
        helper.getLearner().learn(uri);

        LOG.info("Delete terms in domain: " + domain + " ..");
        deleteTerms(domain);

        LOG.info("Retrieving terms from domain..");
        List<Term> terms = new ArrayList<>(helper.getLearner().retrieveTerminology(uri).getTerms());
        if ((terms == null) || (terms.isEmpty())){
            LOG.warn("No terms found in domain: " + uri);
            return;
        }
        LOG.info("Number of terms found in domain: " + terms.size());

        LOG.info("Retrieving relations from domain..");
        List<org.epnoi.model.Relation> relations = new ArrayList<>(helper.getLearner().retrieveRelations(uri).getRelations());
        if ((relations == null) || (relations.isEmpty())){
            LOG.warn("No relations found in domain: " + uri);
            relations = new ArrayList<>();
        }


        final double relationhoodThreshold = helper.getTermThreshoold();
        List<org.epnoi.model.Relation> relevantRelations = relations.stream().filter(relation -> relation.calculateRelationhood() > relationhoodThreshold).collect(Collectors.toList());
        LOG.info("Number of relevant relations found in domain: " + relevantRelations.size());
        List<String> neededTerms = relevantRelations.stream().flatMap(relation -> Arrays.asList(relation.getSource(), relation.getTarget()).stream()).distinct().collect(Collectors.toList());


        final double termhoodThreshold = helper.getTermThreshoold();
        Set<Term> relevantTerms = terms.stream().filter(term -> (term.getAnnotatedTerm().getAnnotation().getTermhood() > termhoodThreshold) || (neededTerms.contains(term.getAnnotatedTerm().getWord()))).filter(term -> term.getAnnotatedTerm().getAnnotation().getLength() < 4).collect(Collectors.toSet());
        LOG.info("Number of relevant terms found in domain: " + relevantRelations.size());



        relevantTerms.forEach(term -> create(term,domain));

        relevantRelations.forEach(relation -> create(relation,domain));

    }


    private URI createRelationalSentencesCorpus(Integer maxSize){
        Parameters<Object> runtimeParameters = new Parameters<Object>();

        // This uri is not the domain uri, is the uri of annotated relational corpus
//        runtimeParameters.setParameter(RelationalSentencesCorpusCreationParameters.RELATIONAL_SENTENCES_CORPUS_URI, uri);
        runtimeParameters.setParameter(RelationalSentencesCorpusCreationParameters.MAX_TEXT_CORPUS_SIZE, maxSize);


        helper.getTrainer().createRelationalSentencesCorpus(runtimeParameters);
        URI createdResourceUri = null;
        if (runtimeParameters.getParameterValue(RelationalSentencesCorpusCreationParameters.RELATIONAL_SENTENCES_CORPUS_URI) != null) {
            createdResourceUri =
                    UriBuilder.fromUri((String) helper.getTrainer().getRuntimeParameters().getParameterValue(RelationalSentencesCorpusCreationParameters.RELATIONAL_SENTENCES_CORPUS_URI)).build();
        } else {
            createdResourceUri =
                    UriBuilder.fromUri((String) helper.getTrainer().getRelationalSentencesCorpusCreationParameters()
                            .getParameterValue(RelationalSentencesCorpusCreationParameters.RELATIONAL_SENTENCES_CORPUS_URI)).build();

        }
        return createdResourceUri;
    }


    private URI createRelationalPatternModel(String path){
        Parameters<Object> runtimeParameters = new Parameters<Object>();
        runtimeParameters.setParameter(RelationalPatternsModelCreationParameters.MODEL_PATH, path);
        helper.getTrainer().createRelationalPatternsModel(runtimeParameters);

        URI uri =
                UriBuilder.fromUri((String) helper.getTrainer().getRelationalSentencesCorpusCreationParameters().getParameterValue(RelationalSentencesCorpusCreationParameters.RELATIONAL_SENTENCES_CORPUS_URI)).build();
        return uri;
    }

    private void deleteTerms(Domain domain){
        LOG.info("Deleting previously discovered terms ..");
        List<String> terms = helper.getUdm().find(Resource.Type.TERM).in(Resource.Type.DOMAIN, domain.getUri());

        if (terms != null && !terms.isEmpty()){
            for (String uri : terms){
                //TODO check if term exist in other domain before to be deleted
                helper.getUdm().delete(Resource.Type.TERM).byUri(uri);
            }
        }

    }

    private void create(org.epnoi.model.Relation relation, Domain domain){
        LOG.info("Trying to create a new relation: " + relation);

        LOG.debug("Source term: " + relation.getSource());
        List<String> termSource = helper.getUdm().find(Resource.Type.TERM).by(org.epnoi.model.domain.resources.Term.CONTENT, relation.getSource());

        LOG.debug("Target term: " + relation.getTarget());
        List<String> termTarget = helper.getUdm().find(Resource.Type.TERM).by(org.epnoi.model.domain.resources.Term.CONTENT, relation.getTarget());

        if (termSource != null && !termSource.isEmpty() && termTarget != null && !termTarget.isEmpty()){
            //TODO take into account the relation type
            HypernymOf hypernym = Relation.newHypernymOf(termSource.get(0), termTarget.get(0));
            hypernym.setDomain(domain.getUri());
            hypernym.setWeight(relation.calculateRelationhood());
            helper.getUdm().save(hypernym);
        }else{
            LOG.warn("No terms found for relation: " + relation);
        }
    }

    private void create(Term term, Domain domain ){
        try{

            LOG.info("Term: " + term);
            // Check if exists in other domain
            List<String> termUris = helper.getUdm().find(Resource.Type.TERM).by(org.epnoi.model.domain.resources.Term.CONTENT, term.getAnnotatedTerm().getWord());

            String termUri;
            if (termUris != null && !termUris.isEmpty()){
                LOG.debug("Term: " + term.getAnnotatedTerm().getWord() + " already exists!");
                //TODO relate to domain if needed
                termUri = termUris.get(0);
            }else{
                // Create the new term
                org.epnoi.model.domain.resources.Term domainTerm = Resource.newTerm();
                domainTerm.setContent(term.getAnnotatedTerm().getWord());
                helper.getUdm().save(domainTerm);
                termUri = domainTerm.getUri();

                // Relate it to words
                for (String word: term.getAnnotatedTerm().getAnnotation().getWords()){

                    // Check if exists
                    List<String> wordUris = helper.getUdm().find(Resource.Type.WORD).by(Word.CONTENT, word);
                    String wordUri;
                    if (wordUris == null || wordUris.isEmpty()){
                        // Create word
                        Word wordDomain = Resource.newWord();
                        wordDomain.setContent(word);
                        helper.getUdm().save(wordDomain);
                        wordUri = wordDomain.getUri();
                    }else{
                        wordUri = wordUris.get(0);
                    }

                    // Relate to term
                    MentionsFromTerm mention = Relation.newMentionsFromTerm(termUri, wordUri);
                    mention.setTimes(1L);
                    mention.setWeight(Double.valueOf(1.0/term.getAnnotatedTerm().getAnnotation().getLength()));
                    helper.getUdm().save(mention);
                }
            }
            // Check if term not related previously to Domain
            // Relate it to Domain
            AppearedIn appeared = Relation.newAppearedIn(termUri, domain.getUri());
            appeared.setTimes(term.getAnnotatedTerm().getAnnotation().getOcurrences());
            appeared.setConsensus(term.getAnnotatedTerm().getAnnotation().getDomainConsensus());
            appeared.setCvalue(term.getAnnotatedTerm().getAnnotation().getCValue());
            appeared.setPertinence(term.getAnnotatedTerm().getAnnotation().getDomainPertinence());
            appeared.setProbability(term.getAnnotatedTerm().getAnnotation().getTermProbability());
            appeared.setSubtermOf(term.getAnnotatedTerm().getAnnotation().getOcurrencesAsSubterm());
            appeared.setSupertermOf(term.getAnnotatedTerm().getAnnotation().getNumberOfSuperterns());
            appeared.setTermhood(term.getAnnotatedTerm().getAnnotation().getTermhood());
            helper.getUdm().save(appeared);

        }catch (Exception e){
            LOG.warn("Unexpected error while processing term: " + term,e);
        }
    }

}


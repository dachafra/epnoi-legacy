package org.epnoi.learner;

import es.cbadenes.lab.test.IntegrationTest;
import org.apache.commons.lang3.StringUtils;
import org.epnoi.knowledgebase.KnowledgeBaseFactory;
import org.epnoi.knowledgebase.KnowledgeBaseHandlerImpl;
import org.epnoi.knowledgebase.wikidata.WikidataHandlerParameters;
import org.epnoi.knowledgebase.wordnet.WordNetHandlerParameters;
import org.epnoi.learner.service.rest.DemoResource;
import org.epnoi.learner.service.rest.LearnerResource;
import org.epnoi.learner.service.rest.TrainerResource;
import org.epnoi.model.KnowledgeBase;
import org.epnoi.model.RelationHelper;
import org.epnoi.model.exceptions.EpnoiInitializationException;
import org.epnoi.model.exceptions.EpnoiResourceAccessException;
import org.epnoi.model.modules.Core;
import org.epnoi.model.modules.KnowledgeBaseParameters;
import org.epnoi.uia.core.CoreUtility;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.ws.rs.core.Response;

/**
 * Created by rgonzalez on 3/12/15.
 */
@Category(IntegrationTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = LearnerConfig.class)
@ActiveProfiles("develop")
@TestPropertySource(properties = {"learner.task.terms.extract = false", "learner.task.terms.store = false", "learner.task.relations.parallel = true"})
public class KnowledgeBaseTest {

    private static Integer MAX_HEADER = 20;

    private static final Logger LOG = LoggerFactory.getLogger(KnowledgeBaseTest.class);

    @Autowired
    DemoResource demoResource;

    @Autowired
    TrainerResource trainerResource;

    @Autowired
    LearnerResource learnerResource;

    @Autowired
    KnowledgeBaseHandlerImpl knowledgeBaseHandler;

    @Value("${learner.demo.harvester.uri}")
    String domainUri;

//    @Value("${learner.corpus.sentences.maxlength}")
//    Integer maxLength;



    @Test
    public void train() throws EpnoiInitializationException, EpnoiResourceAccessException {
        System.out.println("Starting the Knowledge Base test!!");

//        String filepath = "/opt/epnoi/epnoi/epnoideployment/wordnet/dictWN3.1/";
//
//        Core core = CoreUtility.getUIACore();
//
//        KnowledgeBaseParameters knowledgeBaseParameters = new KnowledgeBaseParameters();
//        WikidataHandlerParameters wikidataParameters = new WikidataHandlerParameters();
//
//        WordNetHandlerParameters wordnetParameters = new WordNetHandlerParameters();
//        wordnetParameters.setParameter(
//                WordNetHandlerParameters.DICTIONARY_LOCATION, filepath);
//
//        wikidataParameters.setParameter(
//                WikidataHandlerParameters.WIKIDATA_VIEW_URI,
//                WikidataHandlerParameters.DEFAULT_URI);
//        wikidataParameters.setParameter(
//                WikidataHandlerParameters.STORE_WIKIDATA_VIEW, true);
//        wikidataParameters.setParameter(
//                WikidataHandlerParameters.RETRIEVE_WIKIDATA_VIEW, false);
//        wikidataParameters.setParameter(WikidataHandlerParameters.OFFLINE_MODE,
//                true);
//        wikidataParameters.setParameter(
//                WikidataHandlerParameters.DUMP_FILE_MODE,
//                WikidataHandlerParameters.DumpProcessingMode.JSON);
//        wikidataParameters.setParameter(WikidataHandlerParameters.TIMEOUT, 10);
//        //wikidataParameters.setParameter(WikidataHandlerParameters.DUMP_PATH,"/Users/rafita/Documents/workspace/wikidataParsingTest");
//        wikidataParameters.setParameter(WikidataHandlerParameters.DUMP_PATH,"/opt/epnoi/epnoideployment/wikidata/dumpfiles/wikidata/json-20150413");
//
//        knowledgeBaseParameters.setParameter(
//                KnowledgeBaseParameters.WORDNET_PARAMETERS, wordnetParameters);
//
//        knowledgeBaseParameters
//                .setParameter(KnowledgeBaseParameters.WIKIDATA_PARAMETERS,
//                        wikidataParameters);
//
//        knowledgeBaseParameters.setParameter(
//                KnowledgeBaseParameters.CONSIDER_WIKIDATA, false);
//        knowledgeBaseParameters.setParameter(
//                KnowledgeBaseParameters.CONSIDER_WORDNET, true);
//
//        KnowledgeBaseFactory knowledgeBaseCreator = new KnowledgeBaseFactory();
//        try {
//            knowledgeBaseCreator.init(core, knowledgeBaseParameters);
//        } catch (EpnoiInitializationException e) {
//            System.out.println("The KnowledgeBase couldn't be initialized");
//            e.printStackTrace();
//
//        }
//        KnowledgeBase curatedRelationsTable = knowledgeBaseCreator.build();

        KnowledgeBase knowledgebase = knowledgeBaseHandler.getKnowledgeBase();

        System.out
                .println("Testing for dog-canine-------------------------------------------------------");
        System.out.println(knowledgebase.areRelated("dog", "canrine", RelationHelper.HYPERNYMY));

        System.out
                .println("Testing for dogs-canine-------------------------------------------------------");
        System.out.println(knowledgebase.areRelated("dogs", "canine",RelationHelper.HYPERNYMY));

        System.out
                .println("Testing for dog-canines-------------------------------------------------------");
        System.out.println(knowledgebase.areRelated("dog", "canines ",RelationHelper.HYPERNYMY));

        System.out.println("Starting the CuratedRelationsTableCreator test!!");

    }

}

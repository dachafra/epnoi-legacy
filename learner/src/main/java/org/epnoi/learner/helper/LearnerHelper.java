package org.epnoi.learner.helper;

import lombok.Data;
import org.epnoi.learner.filesystem.DemoDataLoader;
import org.epnoi.learner.modules.Learner;
import org.epnoi.learner.modules.Trainer;
import org.epnoi.storage.UDM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by cbadenes on 21/02/16.
 */
@Data
@Component
public class LearnerHelper {

    @Autowired
    UDM udm;

    @Autowired
    DemoDataLoader demoDataLoader;

    @Autowired
    Learner learner;

    @Autowired
    Trainer trainer;

    @Value("${learner.corpus.sentences.maxlength}")
    Integer maxLength;

    @Value("${learner.task.relations.hypernyms.lexical.path}")
    String modelPath;

    @Value("${learner.task.termhood.min}")
    Double termThreshoold;

    @Value("${learner.task.relationhood.min}")
    Double relationThreshoold;


}

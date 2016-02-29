package org.epnoi.learner.helper;

import lombok.Data;
import org.epnoi.learner.filesystem.DemoDataLoader;
import org.epnoi.learner.filesystem.FilesystemHarvester;
import org.epnoi.learner.modules.Learner;
import org.epnoi.learner.modules.Trainer;
import org.epnoi.model.modules.Core;
import org.epnoi.storage.UDM;
import org.epnoi.storage.generator.URIGenerator;
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
    URIGenerator uriGenerator;

    @Autowired
    Learner learner;

    @Autowired
    Trainer trainer;

    @Autowired
    FilesystemHarvester filesystemHarvester;

    @Value("${learner.corpus.sentences.maxlength}")
    Integer maxLength;

    @Value("${learner.task.relations.hypernyms.lexical.path}")
    String modelPath;

    @Value("${learner.task.termhood.min}")
    Double termThreshoold;

    @Value("${learner.task.relationhood.min}")
    Double relationThreshoold;


}

package dev.surly.ai.collab.nlp;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Slf4j
@Service
public class NlpService {

    public List<String> getSubtasks(String text) {

        log.info("Extracting sub-tasks from: {}", text);

        // Set up pipeline properties
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,parse");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // Create an empty Annotation just with the given text
        Annotation document = new Annotation(text);

        // Run all Annotators on this text
        pipeline.annotate(document);

        List<String> subTasks = new ArrayList<>();

        // Iterate over all of the sentences found
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            // Parse the sentence
            Tree parseTree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);

            // Extract actionable phrases as sub-tasks
            extractActionPhrases(parseTree, subTasks);
        }

        return subTasks;
    }

    private void extractActionPhrases(Tree parseTree, List<String> subTasks) {
        for (Tree subtree : parseTree) {
            if (subtree.label().value().equals("VP")) { // VP stands for Verb Phrase
                StringBuilder taskBuilder = new StringBuilder();
                for (Tree leaf : subtree.getLeaves()) {
                    if (!taskBuilder.isEmpty()) {
                        taskBuilder.append(" ");
                    }
                    taskBuilder.append(leaf.toString());
                }
                String potentialTask = taskBuilder.toString();
                if (!potentialTask.isEmpty() && potentialTask.split(" ").length > 2) { // Filter very short phrases
                    subTasks.add(potentialTask);
                }
            }
        }
    }
}

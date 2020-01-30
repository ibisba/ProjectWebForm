package eu.ibisba.hub.backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IBISBASubmission {
    public static void submission(ArrayList<Workflow> workflows, String ids) {
        List<String> identifiers = Arrays.asList(ids.split(","));

        for (Workflow workflow : workflows) {
            String identifier = String.valueOf(workflow.getAssay().getIdentifier());
            if (identifiers.contains(identifier)) {
                // TODO see if we can preserve the submission procedure not the identifier order as selected as these are "random"
            System.err.println("submitting..." + workflow.getInvestigationTitle() + " " + workflow.getStudyTitle() + " " + workflow.getAssayTitle());
            }
        }
    }
}

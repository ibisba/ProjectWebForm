package eu.ibisba.hub.backend;

import eu.ibisba.hub.backend.ISA.Assay;
import eu.ibisba.hub.backend.ISA.Investigation;
import eu.ibisba.hub.backend.ISA.Study;

public class Workflow {

    private Investigation investigation;
    private String investigationTitle;
    private Study study;
    private String studyTitle;
    private Assay assay;
    private String assayTitle;

    public Workflow(Investigation investigation, Study study, Assay assay) {
        super();
        this.investigation = investigation;
        this.study = study;
        this.assay = assay;
        // Setting the descriptions for the grid
        this.setInvestigationTitle(investigation.getDescription());
        this.setStudyTitle(study.getDescription());
        this.setAssayTitle(assay.getDescription());
    }

    public Workflow() {

    }

    @Override
    public String toString() {
        return investigation.getDescription() + " " + study.getDescription() + "(" + assay.getDescription() + ")";
    }

    public String getInvestigationTitle() {
        return investigationTitle;
    }

    public void setInvestigationTitle(String investigationTitle) {
        this.investigationTitle = investigationTitle;
    }

    public String getStudyTitle() {
        return studyTitle;
    }

    public void setStudyTitle(String studyTitle) {
        this.studyTitle = studyTitle;
    }

    public String getAssayTitle() {
        return assayTitle;
    }

    public void setAssayTitle(String assayTitle) {
        this.assayTitle = assayTitle;
    }

    public Assay getAssay() {
        return assay;
    }
}

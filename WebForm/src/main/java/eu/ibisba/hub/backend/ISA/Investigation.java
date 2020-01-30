package eu.ibisba.hub.backend.ISA;

import java.util.ArrayList;

public class Investigation {
    private String description;
    private ArrayList<Study> study = new ArrayList<>();
    public Investigation(String value) {
        setDescription(value);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Study> getStudy() {
        return study;
    }

    public void addStudy(Study study) {
        this.study.add(study);
    }
}

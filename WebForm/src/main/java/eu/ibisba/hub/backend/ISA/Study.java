package eu.ibisba.hub.backend.ISA;

import java.util.ArrayList;

public class Study {
    private String description;
    private ArrayList<Assay> assay = new ArrayList<>();

    public Study(String description) {
        setDescription(description);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Assay> getAssay() {
        return assay;
    }

    public void addAssay(Assay assay) {
        this.assay.add(assay);
    }
}

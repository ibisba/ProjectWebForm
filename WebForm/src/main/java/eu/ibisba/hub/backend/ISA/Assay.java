package eu.ibisba.hub.backend.ISA;

public class Assay {
    private String description;
    private int identifier;

    public Assay(String description, int identifier) {
        setDescription(description);
        setIdentifier(identifier);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public int getIdentifier() {
        return identifier;
    }
}

package com.group3.taamapp;
import java.util.Map;

public class ExpandedArtifact {

    private String lotNumber;
    private String artifactName;
    private String description;
    private String category;
    private String material;
    private String dynastyPeriod;
    private String culturalOrigin;
    private String dimensions;
    private String conditionReport;
    private String currentLocation;
    private String acquisitionMethod;
    private String provenance;
    private String accessionNumber;
    private String notes;
    private String imageUrl;
    private int likeCount;
    private Map<String, Comment> comments;

    // create an empty ExpandedArtifact object as required by firebase
    public ExpandedArtifact() {

    }

    // the actual constructor to create an ExpandedArtifact object with all info
    public ExpandedArtifact(
            String lotNumber,
            String artifactName,
            String description,
            String category,
            String material,
            String dynastyPeriod,
            String imageUrl,
            int likeCount,
            Map<String, Comment> comments
    ) {
        this.lotNumber = lotNumber;
        this.artifactName = artifactName;
        this.description = description;
        this.category = category;
        this.material = material;
        this.dynastyPeriod = dynastyPeriod;
        this.imageUrl = imageUrl;
        this.likeCount = likeCount;
        this.comments = null;
    }

    // Full constructor. Only lotNumber, artifactName, description, category,
    // material and dynastyPeriod are mandatory; the remaining String fields
    // may be null or empty.
    public ExpandedArtifact(
            String lotNumber,
            String artifactName,
            String description,
            String category,
            String material,
            String dynastyPeriod,
            String culturalOrigin,
            String dimensions,
            String conditionReport,
            String currentLocation,
            String acquisitionMethod,
            String provenance,
            String accessionNumber,
            String notes,
            String imageUrl,
            int likeCount,
            Map<String, Comment> comments
    ) {
        this(lotNumber, artifactName, description, category, material,
                dynastyPeriod, imageUrl, likeCount, comments);
        this.culturalOrigin = culturalOrigin;
        this.dimensions = dimensions;
        this.conditionReport = conditionReport;
        this.currentLocation = currentLocation;
        this.acquisitionMethod = acquisitionMethod;
        this.provenance = provenance;
        this.accessionNumber = accessionNumber;
        this.notes = notes;
        this.comments = comments;
    }


    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }

    public String getArtifactName() {
        return artifactName;
    }

    public void setArtifactName(String artifactName) {
        this.artifactName = artifactName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getDynastyPeriod() {
        return dynastyPeriod;
    }

    public void setDynastyPeriod(String dynastyPeriod) {
        this.dynastyPeriod = dynastyPeriod;
    }

    public String getCulturalOrigin() { return culturalOrigin; }

    public void setCulturalOrigin(String culturalOrigin) { this.culturalOrigin = culturalOrigin; }

    public String getDimensions() { return dimensions; }

    public void setDimensions(String dimensions) { this.dimensions = dimensions; }

    public String getConditionReport() { return conditionReport; }

    public void setConditionReport(String conditionReport) { this.conditionReport = conditionReport; }

    public String getCurrentLocation() { return currentLocation; }

    public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }

    public String getAcquisitionMethod() { return acquisitionMethod; }

    public void setAcquisitionMethod(String acquisitionMethod) { this.acquisitionMethod = acquisitionMethod; }

    public String getProvenance() { return provenance; }

    public void setProvenance(String provenance) { this.provenance = provenance; }

    public String getAccessionNumber() { return accessionNumber; }

    public void setAccessionNumber(String accessionNumber) { this.accessionNumber = accessionNumber; }

    public String getNotes() { return notes; }

    public void setNotes(String notes) { this.notes = notes; }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public Map<String, Comment> getComments() { return comments; }

    public void setComments(Map<String, Comment> comments) { this.comments = comments; }
}


package com.group3.taamapp;


public class ExpandedArtifact {

    private String id;
    private String lotNumber;
    private String artifactName;
    private String description;
    private String category;
    private String material;
    private String dynastyPeriod;
    private String imageUrl;
    private int likeCount;
    private int saveCount;

    public ExpandedArtifact() {

    }

    public ExpandedArtifact(
            String id,
            String lotNumber,
            String artifactName,
            String description,
            String category,
            String material,
            String dynastyPeriod,
            String imageUrl,
            int likeCount,
            int saveCount
    ) {
        this.id = id;
        this.lotNumber = lotNumber;
        this.artifactName = artifactName;
        this.description = description;
        this.category = category;
        this.material = material;
        this.dynastyPeriod = dynastyPeriod;
        this.imageUrl = imageUrl;
        this.likeCount = likeCount;
        this.saveCount = saveCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public void setSaveCount(int saveCount) {
        this.saveCount = saveCount;
    }

}



package zamestnancidb.model;

public class Collaboration {

    private final int partnerID;
    private CollaborationQuality quality;

    public Collaboration(int partnerID, CollaborationQuality quality) {
        this.partnerID = partnerID;
        this.quality = quality;
    }

    public int getPartnerID() { return partnerID; }
    public CollaborationQuality getQuality() { return quality; }
    public void setQuality(CollaborationQuality quality) { this.quality = quality; }

    @Override
    public String toString() {
        return "Partner ID=" + partnerID + ", kvalita=" + quality.getLabel();
    }
}
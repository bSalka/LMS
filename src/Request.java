import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import org.bson.Document;

public class Request {
    private String id;
    private String reason;
    private String approved;
    private Date startDate;
    private Date endDate;

    public Request(String reason, String approved, Date startDate, Date endDate) {
        this.id = UUID.randomUUID().toString();
        setReason(reason);
        setApproved(approved);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Request(String requestId, String reason, String startDateStr, String endDateStr, String approved) {
        this.id = requestId;
        setReason(reason);

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            this.startDate = sdf.parse(startDateStr);
            this.endDate = sdf.parse(endDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            this.startDate = null;
            this.endDate = null;
        }

        setApproved(approved);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        if (!reason.equals("zdravstveni razlog") && !reason.equals("praznik") && !reason.equals("godišnji odmor")) {
            throw new IllegalArgumentException("Razlog mora biti 'zdravstveni razlog', 'praznik' ili 'godišnji odmor'.");
        }
        this.reason = reason;
    }

    public String getApproved() {
        return approved;
    }

    public void setApproved(String approved) {
        if (!approved.equals("čekanje") && !approved.equals("odobreno") && !approved.equals("odbijeno")) {
            throw new IllegalArgumentException("Status mora biti 'čekanje', 'odobreno' ili 'odbijeno'.");
        }
        this.approved = approved;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "Request{" +
                "id='" + id + '\'' +
                ", reason='" + reason + '\'' +
                ", approved='" + approved + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }

    // Metoda za prebacivanje Request objekta u MongoDB Document
    public Document toDocument() {
        Document doc = new Document();
        doc.append("requestId", id != null ? id : "");
        doc.append("reason", reason != null ? reason : "");
        doc.append("startDate", startDate != null ? new SimpleDateFormat("yyyy-MM-dd").format(startDate) : null);
        doc.append("endDate", endDate != null ? new SimpleDateFormat("yyyy-MM-dd").format(endDate) : null);
        doc.append("approved", approved != null ? approved : "čekanje");
        return doc;
    }


    public void updateRequest(String reason, String startDateStr, String endDateStr, String approved) {
        setReason(reason);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            this.startDate = sdf.parse(startDateStr);
            this.endDate = sdf.parse(endDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        setApproved(approved);
    }

    public Object getStartRequestDate() {
        return startDate;
    }

    public Object getEndRequestDate() {
        return endDate;
    }

    public Object getRequestId() {
        return id;
    }
}
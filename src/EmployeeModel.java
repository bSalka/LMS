import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;

public class EmployeeModel {
    private String id;
    private String name;
    private String surname;
    private String email;
    private String password;
    private ArrayList<Request> requests;
    private String role;

    public EmployeeModel(String id, String name, String surname, String email, String password, ArrayList<Request> requests, String role) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.requests = requests;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getRole() {
        return role;
    }

    public ArrayList<Request> getRequests() {
        return requests;
    }

    public String getEmployeeId() {
        return id;
    }

    // autentifikaciju korisnika
    public static EmployeeModel getEmployeeByUsernameAndPassword(String username, String password) {
        MongoDBController mongoDBController = MongoDBController.getInstance();
        MongoDatabase database = mongoDBController.getDatabase();
        MongoCollection<Document> usersCollection = database.getCollection("LMSproject");

        Document query = new Document("username", username).append("password", password);
        Document userDoc = usersCollection.find(query).first();

        if (userDoc == null) {
            System.out.println("User not found.");
            return null;
        }

        String id = userDoc.getObjectId("_id").toString(); // Koristi `_id` umjesto `id`
        String name = userDoc.getString("firstName");
        String surname = userDoc.getString("lastName");
        String email = userDoc.getString("username");
        String passwordDb = userDoc.getString("password");
        String role = userDoc.getString("role");

        if (id == null) {
            System.out.println("ID is null for user: " + username);
        } else {
            System.out.println("Employee ID from database: " + id);
        }

        ArrayList<Request> requests = new ArrayList<>();
        if (userDoc.containsKey("requests")) {
            ArrayList<Document> requestDocs = (ArrayList<Document>) userDoc.get("requests");
            for (Document requestDoc : requestDocs) {
                String requestId = requestDoc.getString("requestId");
                String reason = requestDoc.getString("reason");
                String startDate = requestDoc.getString("startDate");
                String endDate = requestDoc.getString("endDate");
                String approved = requestDoc.containsKey("approved") ? requestDoc.getString("approved") : "čekanje";

                Request request = new Request(requestId, reason, startDate, endDate, approved);
                requests.add(request);
            }
        }

        EmployeeModel employee = new EmployeeModel(id, name, surname, email, passwordDb, requests, role);
        System.out.println("Employee ID after initialization: " + employee.getEmployeeId());
        return employee;
    }

    // update zahtjeva u modelu
    public boolean updateRequest(String requestId, String reason, String startDateStr, String endDateStr, String approved) {
        for (Request request : requests) {
            if (request.getId().equals(requestId)) {
                request.updateRequest(reason, startDateStr, endDateStr, approved);

                saveRequestsToDatabase();
                return true;
            }
        }
        return false;
    }

    //delete zahtjeva
    public boolean deleteRequest(String requestId) {
        for (Request request : requests) {
            if (request.getId().equals(requestId)) {
                requests.remove(request);

                MongoDBController mongoDBController = MongoDBController.getInstance();
                mongoDBController.deleteRequestFromDatabase(requestId, id);  // Poziv za brisanje iz baze

                saveRequestsToDatabase();
                return true;
            }
        }
        return false;
    }

    //save zahtjeva u bazu
    public void saveRequestsToDatabase() {
        MongoDBController mongoDBController = MongoDBController.getInstance();
        MongoDatabase database = mongoDBController.getDatabase();
        MongoCollection<Document> usersCollection = database.getCollection("LMSproject");

        ArrayList<Document> requestDocs = new ArrayList<>();
        for (Request request : requests) {
            requestDocs.add(request.toDocument());
        }

        Document updateQuery = new Document("_id", new ObjectId(id));  // Koristi `_id`
        Document updateData = new Document("$set", new Document("requests", requestDocs));

        usersCollection.updateOne(updateQuery, updateData);
    }
}
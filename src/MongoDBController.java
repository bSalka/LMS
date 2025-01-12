import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

public class MongoDBController {

    private static final String CONNECTION_STRING = "mongodb+srv://belma7salkicic:hHjrRZJYjYxcZ5WW@cluster0.l1nhc.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";
    private static MongoDBController instance;
    private final MongoClient mongoClient;
    private final MongoDatabase database;

    private MongoDBController() {
        try {
            ServerApi serverApi = ServerApi.builder()
                    .version(ServerApiVersion.V1)
                    .build();

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(CONNECTION_STRING))
                    .serverApi(serverApi)
                    .build();

            mongoClient = MongoClients.create(settings);
            database = mongoClient.getDatabase("LMS");

            database.runCommand(new Document("ping", 1));
            System.out.println("Successfully connected to MongoDB!");

        } catch (MongoException e) {
            e.printStackTrace();
            throw new RuntimeException("MongoDB connection failed");
        }
    }

    public static synchronized MongoDBController getInstance() {
        if (instance == null) {
            instance = new MongoDBController();
        }
        return instance;
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("MongoDB connection closed.");
        }
    }

    public void addRequest(Request request, String employeeId) {
        try {
            // Prebacivanje Request objekta u MongoDB Document
            Document requestDoc = request.toDocument();

            // Postavljanje ispravnog employeeId u zahtjev
            requestDoc.append("employeeId", employeeId);  // Correctly assign employeeId to the request

            // Dodavanje dokumenta u kolekciju "requests"
            MongoCollection<Document> requestsCollection = database.getCollection("requests");
            requestsCollection.insertOne(requestDoc);
            System.out.println("Request successfully added to MongoDB!");

            // Ažurirajte zaposleni dokument dodajući novi zahtjev u polje "requests"
            MongoCollection<Document> usersCollection = database.getCollection("LMSproject");
            Document employeeQuery = new Document("_id", new ObjectId(employeeId));  // Koristi `_id` za identifikaciju
            Document update = new Document("$push", new Document("requests", requestDoc));  // Dodajemo novi zahtjev

            // Dodavanje ispisnih naredbi za provjeru
            System.out.println("Employee ID: " + employeeId);
            System.out.println("Employee Query: " + employeeQuery.toJson());
            System.out.println("Update Data: " + update.toJson());

            // Ako je "employeeId" ispravno postavljen, zahtjev bi trebao biti dodan odgovarajućem zaposlenom
            usersCollection.updateOne(employeeQuery, update);
            System.out.println("Employee's requests updated in the database.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error adding request to MongoDB.");
        }
    }

    public void deleteRequestFromDatabase(String requestId, String employeeId) {
        try {
            MongoCollection<Document> usersCollection = database.getCollection("LMSproject");
            Document employeeQuery = new Document("_id", new ObjectId(employeeId));  // Koristi `_id`

            // Prvo ukloniti zahtjev iz "requests" polja zaposlenog
            Document update = new Document("$pull", new Document("requests", new Document("requestId", requestId)));
            usersCollection.updateOne(employeeQuery, update);
            System.out.println("Request successfully deleted from database.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error deleting request from database.");
        }
    }
}

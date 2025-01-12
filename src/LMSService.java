import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class LMSService {
    private final MongoDatabase database;
    private final MongoCollection<Document> collection;

    public LMSService() {
        this.database = MongoDBController.getInstance().getDatabase();
        this.collection = database.getCollection("users");
    }

    public EmployeeModel authenticateUser(String username, String password) {
        return EmployeeModel.getEmployeeByUsernameAndPassword(username, password);
    }
}

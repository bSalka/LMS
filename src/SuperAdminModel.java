import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.util.List;
import java.util.Arrays;

public class SuperAdminModel {

    private MongoDatabase database;

    public SuperAdminModel() {
        database = MongoDBController.getInstance().getDatabase();
    }

    public void loadDataIntoTable(JTable table) {
        MongoCollection<Document> usersCollection = database.getCollection("LMSproject");

        List<String> roles = Arrays.asList("employee", "manager");
        List<Document> users = usersCollection.find(new Document("role", new Document("$in", roles)))
                .into(new java.util.ArrayList<>());

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Ime");
        model.addColumn("Prezime");
        model.addColumn("Username");
        model.addColumn("Role");

        for (Document user : users) {
            Object[] row = new Object[5];

            Object id = user.get("_id");
            if (id instanceof String) {
                row[0] = id.toString();
            } else if (id instanceof ObjectId) {
                row[0] = ((ObjectId) id).toString();
            }

            row[1] = user.getString("firstName");
            row[2] = user.getString("lastName");
            row[3] = user.getString("username");
            row[4] = user.getString("role");

            model.addRow(row);
        }


        table.setModel(model);
    }

    // update korisnika
    public void updateUser(String oldUsername, String newUsername, String name, String surname, String password, String role) {
        MongoCollection<Document> usersCollection = database.getCollection("LMSproject");

        Document existingUserQuery = new Document("username", newUsername);
        long count = usersCollection.countDocuments(existingUserQuery);
        if (count > 0) {
            JOptionPane.showMessageDialog(null, "Korisničko ime već postoji. Molimo izaberite drugo.", "Greška", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Document updatedUser = new Document("firstName", name)
                .append("lastName", surname)
                .append("password", password)
                .append("role", role)
                .append("username", newUsername);

        Document query = new Document("username", oldUsername);
        usersCollection.updateOne(query, new Document("$set", updatedUser));
    }

    //delete korisnika
    public void deleteUser(String userId) {
        MongoCollection<Document> usersCollection = database.getCollection("LMSproject");
        Document query = new Document("_id", new ObjectId(userId));
        usersCollection.deleteOne(query);
    }
}

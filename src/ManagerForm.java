import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ManagerForm {
    public JButton logOutButton;
    public JPanel managerPanel;
    public JTable managerTable;
    private JLabel titleLabel;
    private JButton approveRequestButton;

    public ManagerForm() {
        managerPanel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        logOutButton = new JButton("Log Out");
        titleLabel = new JLabel("Dobrodošli, Manager!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));

        approveRequestButton = new JButton("Approve Request");
        approveRequestButton.setFont(new Font("Arial", Font.PLAIN, 14));
        approveRequestButton.setBackground(logOutButton.getBackground());
        approveRequestButton.setForeground(logOutButton.getForeground());

        topPanel.add(logOutButton);
        topPanel.add(approveRequestButton);
        topPanel.add(titleLabel);

        managerPanel.add(topPanel, BorderLayout.NORTH);

        String[] columns = {"Ime", "Prezime", "ID Zahtjeva", "Razlog", "Početak", "Kraj", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        managerTable = new JTable(tableModel);

        JScrollPane tableScrollPane = new JScrollPane(managerTable);
        managerPanel.add(tableScrollPane, BorderLayout.CENTER);

        loadRequestsToTable();

        managerTable.getSelectionModel().addListSelectionListener(e -> {
        });

        approveRequestButton.addActionListener(e -> {
            int row = managerTable.getSelectedRow();
            if (row != -1) {
                String requestId = (String) managerTable.getValueAt(row, 2);

                int option = JOptionPane.showConfirmDialog(managerPanel,
                        "Da li želite odobriti ili odbiti zahtjev?",
                        "Potvrda",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (option == JOptionPane.YES_OPTION) {
                    updateRequestStatus(requestId, "odobreno");
                } else if (option == JOptionPane.NO_OPTION) {
                    updateRequestStatus(requestId, "odbijeno");
                }
            } else {
                JOptionPane.showMessageDialog(managerPanel, "Niste selektovali zahtjev!", "Greška", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Logout akcija
        logOutButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Odjavljujete se iz sistema.", "Odjava", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        });
    }

    private void loadRequestsToTable() {
        DefaultTableModel tableModel = (DefaultTableModel) managerTable.getModel();
        tableModel.setRowCount(0);

        MongoDBController mongoDBController = MongoDBController.getInstance();
        MongoDatabase database = mongoDBController.getDatabase();
        MongoCollection<Document> usersCollection = database.getCollection("LMSproject");

        List<Document> users = usersCollection.find().into(new ArrayList<>());
        for (Document user : users) {
            // Provjera da li korisnik ima zahtjeve
            if (user.containsKey("requests")) {
                String firstName = user.getString("firstName");
                String lastName = user.getString("lastName");

                List<Document> requests = (List<Document>) user.get("requests");
                for (Document requestDoc : requests) {
                    String requestId = requestDoc.getString("requestId");
                    String reason = requestDoc.getString("reason");
                    String startDate = requestDoc.getString("startDate");
                    String endDate = requestDoc.getString("endDate");
                    String approved = requestDoc.getString("approved");

                    tableModel.addRow(new Object[]{firstName, lastName, requestId, reason, startDate, endDate, approved});
                }
            }
        }
    }

    private void updateRequestStatus(String requestId, String newStatus) {
        MongoDBController mongoDBController = MongoDBController.getInstance();
        MongoDatabase database = mongoDBController.getDatabase();
        MongoCollection<Document> usersCollection = database.getCollection("LMSproject");

        // Pronađi korisnika sa zahtjevom
        List<Document> users = usersCollection.find().into(new ArrayList<>());
        for (Document user : users) {
            if (user.containsKey("requests")) {
                List<Document> requests = (List<Document>) user.get("requests");
                for (Document requestDoc : requests) {
                    String reqId = requestDoc.getString("requestId");
                    if (reqId.equals(requestId)) {
                        // Ažuriraj status zahtjeva
                        requestDoc.put("approved", newStatus);

                        usersCollection.replaceOne(new Document("requests.requestId", requestId), user);

                        // Ažuriraj tabelu
                        DefaultTableModel tableModel = (DefaultTableModel) managerTable.getModel();
                        for (int i = 0; i < tableModel.getRowCount(); i++) {
                            if (tableModel.getValueAt(i, 2).equals(requestId)) {
                                tableModel.setValueAt(newStatus, i, 6);
                                break;
                            }
                        }
                        return;
                    }
                }
            }
        }
    }
}

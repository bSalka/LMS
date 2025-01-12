import javax.swing.*;
import org.bson.Document;

public class AddUserForm {
    public JPanel addUserPanel;
    public JTextField nameTextField;
    public JTextField surnameTextField;
    public JTextField usernameTextField;
    public JTextField passwordTextField;
    public JComboBox<String> roleComboBox;
    public JButton submitButton;

    public JLabel nameLabel;
    public JLabel surnameLabel;
    public JLabel usernameLabel;
    public JLabel passwordLabel;
    public JLabel roleLabel;
    public JLabel welcomeLabel;

    private SuperAdminForm superAdminForm;

    public AddUserForm(SuperAdminForm superAdminForm) {
        this.superAdminForm = superAdminForm;

        nameLabel = new JLabel("Ime:");
        surnameLabel = new JLabel("Prezime:");
        usernameLabel = new JLabel("Username:");
        passwordLabel = new JLabel("Password:");
        roleLabel = new JLabel("Role:");
        welcomeLabel = new JLabel("Dobrodošli! Unesite podatke za novog korisnika:");

        nameTextField = new JTextField(20);
        surnameTextField = new JTextField(20);
        usernameTextField = new JTextField(20);
        passwordTextField = new JTextField(20);
        roleComboBox = new JComboBox<>(new String[] {"employee", "manager"});

        submitButton = new JButton("Dodaj Korisnika");

        addUserPanel = new JPanel();
        GroupLayout layout = new GroupLayout(addUserPanel);
        addUserPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(welcomeLabel)
                .addComponent(nameLabel).addComponent(nameTextField)
                .addComponent(surnameLabel).addComponent(surnameTextField)
                .addComponent(usernameLabel).addComponent(usernameTextField)
                .addComponent(passwordLabel).addComponent(passwordTextField)
                .addComponent(roleLabel).addComponent(roleComboBox)
                .addComponent(submitButton));

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(welcomeLabel)
                .addComponent(nameLabel).addComponent(nameTextField)
                .addComponent(surnameLabel).addComponent(surnameTextField)
                .addComponent(usernameLabel).addComponent(usernameTextField)
                .addComponent(passwordLabel).addComponent(passwordTextField)
                .addComponent(roleLabel).addComponent(roleComboBox)
                .addComponent(submitButton));

        submitButton.addActionListener(e -> addUserToDatabase());
    }

    private void addUserToDatabase() {
        String firstName = nameTextField.getText();
        String lastName = surnameTextField.getText();
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();
        String role = (String) roleComboBox.getSelectedItem();

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Sva polja moraju biti popunjena!");
            return;
        }

        Document newUser = new Document("firstName", firstName)
                .append("lastName", lastName)
                .append("username", username)
                .append("password", password)
                .append("role", role);

        try {
            MongoDBController.getInstance().getDatabase().getCollection("LMSproject").insertOne(newUser);
            JOptionPane.showMessageDialog(null, "Korisnik uspešno dodat!");

            superAdminForm.refreshTable();

            ((JFrame) SwingUtilities.getWindowAncestor(addUserPanel)).dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Greška prilikom dodavanja korisnika.", "Greška", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public JPanel getAddUserPanel() {
        return addUserPanel;
    }
}



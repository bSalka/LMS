import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EditUserForm {
    private JLabel welcomeLabel;
    private JLabel nameLabel;
    private JLabel surnameLabel;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JLabel roleLabel;
    private JTextField nameTextField;
    private JTextField surnameTextField;
    private JTextField usernameTextField;
    private JTextField passwordTextField;
    private JComboBox<String> roleComboBox;
    private JButton saveButton;

    private SuperAdminForm superAdminForm;
    private String username;

    public EditUserForm(SuperAdminForm superAdminForm, String username, String name, String surname, String password, String role) {
        this.superAdminForm = superAdminForm;
        this.username = username;

        welcomeLabel = new JLabel("Uredi Podatke o Korisniku");
        nameLabel = new JLabel("Ime:");
        surnameLabel = new JLabel("Prezime:");
        usernameLabel = new JLabel("Username:");
        passwordLabel = new JLabel("Password:");
        roleLabel = new JLabel("Role:");

        nameTextField = new JTextField(name, 20);
        surnameTextField = new JTextField(surname, 20);
        usernameTextField = new JTextField(username, 20);
        passwordTextField = new JTextField(password, 20);
        roleComboBox = new JComboBox<>(new String[]{"employee", "manager"});

        saveButton = new JButton("Spremi");

        // Layout za formu
        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(welcomeLabel)
                .addComponent(nameLabel).addComponent(nameTextField)
                .addComponent(surnameLabel).addComponent(surnameTextField)
                .addComponent(usernameLabel).addComponent(usernameTextField)
                .addComponent(passwordLabel).addComponent(passwordTextField)
                .addComponent(roleLabel).addComponent(roleComboBox)
                .addComponent(saveButton));

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(welcomeLabel)
                .addComponent(nameLabel).addComponent(nameTextField)
                .addComponent(surnameLabel).addComponent(surnameTextField)
                .addComponent(usernameLabel).addComponent(usernameTextField)
                .addComponent(passwordLabel).addComponent(passwordTextField)
                .addComponent(roleLabel).addComponent(roleComboBox)
                .addComponent(saveButton));

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameTextField.getText();
                String surname = surnameTextField.getText();
                String password = passwordTextField.getText();
                String role = (String) roleComboBox.getSelectedItem();
                String newUsername = usernameTextField.getText();

                if (name.isEmpty() || surname.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "Svi podaci moraju biti uneseni.", "Greška", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                SuperAdminModel superAdminModel = new SuperAdminModel();
                superAdminModel.updateUser(username, newUsername, name, surname, password, role);

                superAdminForm.refreshTable();

                JOptionPane.showMessageDialog(panel, "Podaci korisnika su uspješno ažurirani!", "Informacija", JOptionPane.INFORMATION_MESSAGE);
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(panel);
                topFrame.dispose();
            }
        });

        JFrame frame = new JFrame("Uredi Korisnika");
        frame.setContentPane(panel);
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }
}

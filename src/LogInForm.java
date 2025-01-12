import javax.swing.*;
import javax.swing.border.EmptyBorder;


public class LogInForm {
    private JPanel loginPanel;
    private JTextField usernameTextField;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JPasswordField passwordField;
    private JLabel lmsLabel;
    private JButton loginButton;
    private LMSService lmsService;

    public LogInForm() {
        lmsService = new LMSService();  // Inicijalizacija LMSService

        loginPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        loginButton.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String username = usernameTextField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Molimo unesite korisničko ime i lozinku.", "Greška", JOptionPane.ERROR_MESSAGE);
            return;
        }
        authenticateUser(username, password);
    }

    //poziva iz lms
    private void authenticateUser(String username, String password) {
        try {
            EmployeeModel employee = lmsService.authenticateUser(username, password);

            if (employee == null) {
                JOptionPane.showMessageDialog(null, "Korisnik nije pronađen u bazi.", "Greška", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String role = employee.getRole();

            switch (role) {
                case "employee":
                    openEmployeeForm(employee);
                    break;
                case "manager":
                    openManagerForm();
                    break;
                case "superAdmin":
                    openSuperAdminForm();
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Nepoznata uloga korisnika.", "Greška", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Došlo je do greške prilikom autentifikacije.", "Greška", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // employee panel
    private void openEmployeeForm(EmployeeModel employeeModel) {
        JFrame employeeFrame = new JFrame("Employee Panel");
        EmployeeForm employeeForm = new EmployeeForm(employeeModel);

        employeeFrame.setContentPane((JPanel) employeeForm.getEmployeePanel());
        employeeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        employeeFrame.pack();
        employeeFrame.setLocationRelativeTo(null);
        employeeFrame.setVisible(true);
    }

    //menadzer panel
    private void openManagerForm() {
        JFrame managerFrame = new JFrame("Manager Panel");
        ManagerForm managerForm = new ManagerForm();

        managerFrame.setContentPane(managerForm.managerPanel);
        managerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        managerFrame.setSize(600, 500);
        managerFrame.setLocationRelativeTo(null);
        managerFrame.setVisible(true);
    }

    //superadmin panel
    private void openSuperAdminForm() {
        JFrame superAdminFrame = new JFrame("SuperAdmin Panel");
        SuperAdminForm superAdminForm = new SuperAdminForm();

        superAdminFrame.setContentPane(superAdminForm.superAdminPanel);
        superAdminFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        superAdminFrame.setSize(800, 550);
        superAdminFrame.setLocationRelativeTo(null);
        superAdminFrame.setVisible(true);
    }
    public static void main(String[] args) {
        JFrame frame = new JFrame("LogInForm");
        frame.setContentPane(new LogInForm().loginPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();

        frame.setSize(400, 250);
        frame.setLocationRelativeTo(null);

        frame.setVisible(true);

    }
}

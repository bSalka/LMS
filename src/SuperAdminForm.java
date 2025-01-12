import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SuperAdminForm {
    public JButton logOutButton;
    public JPanel superAdminPanel;
    public JTable superAdminTable;
    public JButton addUserButton;
    public JButton editUserButton;
    public JButton deleteUserButton;

    private SuperAdminModel superAdminModel;

    public SuperAdminForm() {
        superAdminModel = new SuperAdminModel();
        superAdminPanel = new JPanel();
        superAdminPanel.setLayout(new BorderLayout());
        //naslov
        JLabel titleLabel = new JLabel("Dobrodošli Muki Mujić", SwingConstants.CENTER);
        superAdminPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        logOutButton = new JButton("Log Out");
        addUserButton = new JButton("Dodaj Korisnika");
        editUserButton = new JButton("Uredi Korisnika");
        deleteUserButton = new JButton("Obriši Korisnika");

        // Log out
        logOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Odjavljujete se iz sistema.", "Odjava", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            }
        });

        // add user
        addUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Otvara AddUserForm u novom prozoru
                JFrame addUserFrame = new JFrame("Dodaj Korisnika");
                AddUserForm addUserForm = new AddUserForm(SuperAdminForm.this);  // Prosljeđujemo ovu instancu
                addUserFrame.setContentPane(addUserForm.getAddUserPanel());
                addUserFrame.setSize(500, 500);
                addUserFrame.setLocationRelativeTo(null);
                addUserFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                addUserFrame.setVisible(true);
            }
        });

        // edit user
        editUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = superAdminTable.getSelectedRow();
                if (selectedRow != -1) {
                    String username = (String) superAdminTable.getValueAt(selectedRow, 3);
                    String name = (String) superAdminTable.getValueAt(selectedRow, 1);
                    String surname = (String) superAdminTable.getValueAt(selectedRow, 2);
                    String password = (String) superAdminTable.getValueAt(selectedRow, 4);

                    String role = (String) superAdminTable.getValueAt(selectedRow, 4);

                    new EditUserForm(SuperAdminForm.this, username, name, surname, password, role);
                } else {
                    JOptionPane.showMessageDialog(null, "Nema selektovanog korisnika za uređivanje.", "Greška", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // delete user
        deleteUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = superAdminTable.getSelectedRow();
                if (selectedRow != -1) {
                    String userId = (String) superAdminTable.getValueAt(selectedRow, 0);

                    int confirmed = JOptionPane.showConfirmDialog(null, "Da li želite obrisati ovog korisnika?",
                            "Potvrda brisanja", JOptionPane.YES_NO_OPTION);
                    if (confirmed == JOptionPane.YES_OPTION) {
                        superAdminModel.deleteUser(userId);
                        refreshTable();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Nema selektovanog korisnika za brisanje.",
                            "Greška", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        buttonPanel.add(logOutButton);
        buttonPanel.add(addUserButton);
        buttonPanel.add(editUserButton);
        buttonPanel.add(deleteUserButton);

        superAdminPanel.add(buttonPanel, BorderLayout.CENTER);

        superAdminTable = new JTable();
        superAdminModel.loadDataIntoTable(superAdminTable);

        JScrollPane tableScrollPane = new JScrollPane(superAdminTable);
        superAdminPanel.add(tableScrollPane, BorderLayout.SOUTH);
    }

    // refresh table
    public void refreshTable() {
        superAdminModel.loadDataIntoTable(superAdminTable);
        superAdminTable.repaint();
    }
}

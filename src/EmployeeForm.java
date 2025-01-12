import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class EmployeeForm {
    private JPanel employeePanel;
    private JButton logOutButton;
    private JButton addRequestButton;
    private JButton editRequestButton;
    private JButton deleteRequestButton;
    private JTable requestTable;
    private JLabel employeeLabel;

    private EmployeeModel employeeModel;

    public EmployeeForm(EmployeeModel employeeModel) {
        this.employeeModel = employeeModel;
        employeePanel = new JPanel(new BorderLayout());

        employeeLabel = new JLabel("Dobrodošli " + employeeModel.getName() + " " + employeeModel.getSurname() + "!");
        employeePanel.add(employeeLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        logOutButton = new JButton("Log Out");
        addRequestButton = new JButton("Dodaj Zahtjev");
        editRequestButton = new JButton("Uredi Zahtjev");
        deleteRequestButton = new JButton("Obriši Zahtjev");

        buttonPanel.add(logOutButton);
        buttonPanel.add(addRequestButton);
        buttonPanel.add(editRequestButton);
        buttonPanel.add(deleteRequestButton);

        employeePanel.add(buttonPanel, BorderLayout.CENTER);

        requestTable = new JTable();
        populateRequestTable(employeeModel.getRequests());

        JScrollPane scrollPane = new JScrollPane(requestTable);
        employeePanel.add(scrollPane, BorderLayout.SOUTH);

        logOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Odjavljujete se iz sistema.", "Odjava", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            }
        });

        addRequestButton.addActionListener(e -> {
            System.out.println("Employee ID before creating AddRequestForm: " + employeeModel.getEmployeeId());
            JFrame addRequestFrame = new JFrame("Dodaj Zahtjev");
            AddRequestForm addRequestForm = new AddRequestForm(employeeModel, this);
            addRequestFrame.setContentPane(addRequestForm.getAddRequestPanel());
            addRequestFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            addRequestFrame.setSize(400, 300);
            addRequestFrame.setLocationRelativeTo(null);
            addRequestFrame.setVisible(true);
        });

        editRequestButton.addActionListener(e -> {
            int selectedRow = requestTable.getSelectedRow();
            if (selectedRow != -1) {
                String requestId = (String) requestTable.getValueAt(selectedRow, 0);
                String reason = (String) requestTable.getValueAt(selectedRow, 1);
                String startDateStr = (String) requestTable.getValueAt(selectedRow, 2);
                String endDateStr = (String) requestTable.getValueAt(selectedRow, 3);

                String approvedStr = (String) requestTable.getValueAt(selectedRow, 4);

                JFrame editRequestFrame = new JFrame("Uredi Zahtjev");
                editRequest editRequestForm = new editRequest(employeeModel, this, requestId, reason, startDateStr, endDateStr, approvedStr);
                editRequestFrame.setContentPane(editRequestForm.getEditRequestPanel());
                editRequestFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                editRequestFrame.setSize(400, 300);
                editRequestFrame.setLocationRelativeTo(null);
                editRequestFrame.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "Nema selektovanog zahtjeva za uređivanje.", "Greška", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteRequestButton.addActionListener(e -> {
            int selectedRow = requestTable.getSelectedRow();
            if (selectedRow != -1) {
                int confirmed = JOptionPane.showConfirmDialog(null, "Da li želite obrisati ovaj zahtjev?", "Potvrda brisanja", JOptionPane.YES_NO_OPTION);
                if (confirmed == JOptionPane.YES_OPTION) {
                    String requestId = (String) requestTable.getValueAt(selectedRow, 0);
                    employeeModel.deleteRequest(requestId);
                    populateRequestTable(employeeModel.getRequests());
                }
            } else {
                JOptionPane.showMessageDialog(null, "Nema selektovanog zahtjeva za brisanje.", "Greška", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public void populateRequestTable(ArrayList<Request> requests) {
        String[] columns = {"Request ID", "Razlog izostanka", "Početak izostanka", "Kraj izostanka", "Status"};

        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (Request request : requests) {
            Object[] row = new Object[5];
            row[0] = request.getRequestId();
            row[1] = request.getReason();
            row[2] = request.getStartDate() != null ? sdf.format(request.getStartDate()) : "";
            row[3] = request.getEndDate() != null ? sdf.format(request.getEndDate()) : "";
            row[4] = request.getApproved(); // Status kao string ("čekanje", "odobreno", "odbijeno")
            tableModel.addRow(row);
        }

        requestTable.setModel(tableModel);
        requestTable.setAutoCreateRowSorter(true);
        requestTable.getTableHeader().setReorderingAllowed(false);
    }

    public JPanel getEmployeePanel() {
        return employeePanel;
    }
}
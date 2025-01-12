import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class editRequest {
    private JPanel editRequestPanel;
    private JComboBox<String> reasonComboBox;
    private JTextField startDateTextField;
    private JTextField endDateTextField;
    private JButton saveButton;
    private JLabel editLabel;
    private JLabel reasonLabel;
    private JLabel startDateLabel;
    private JLabel endDateLabel;

    private EmployeeModel employeeModel;
    private EmployeeForm employeeForm;
    private String requestId;
    private String oldReason;
    private String oldStartDateStr;
    private String oldEndDateStr;
    private String oldApprovedStatus;

    public editRequest(EmployeeModel employeeModel, EmployeeForm employeeForm, String requestId, String reason, String startDateStr, String endDateStr, String approvedStatus) {
        this.employeeModel = employeeModel;
        this.employeeForm = employeeForm;
        this.requestId = requestId;
        this.oldReason = reason;
        this.oldStartDateStr = startDateStr;
        this.oldEndDateStr = endDateStr;
        this.oldApprovedStatus = approvedStatus;

        editRequestPanel = new JPanel();
        reasonComboBox = new JComboBox<>(new String[]{"zdravstveni razlog", "praznik", "godišnji odmor"});
        startDateTextField = new JTextField(10);
        endDateTextField = new JTextField(10);
        saveButton = new JButton("Spremi");

        editLabel = new JLabel("Uredi Zahtjev");
        reasonLabel = new JLabel("Razlog izostanka:");
        startDateLabel = new JLabel("Početak izostanka:");
        endDateLabel = new JLabel("Kraj izostanka:");

        reasonComboBox.setSelectedItem(reason);
        startDateTextField.setText(startDateStr);
        endDateTextField.setText(endDateStr);

        editRequestPanel.setLayout(new BoxLayout(editRequestPanel, BoxLayout.Y_AXIS));
        editRequestPanel.add(editLabel);
        editRequestPanel.add(reasonLabel);
        editRequestPanel.add(reasonComboBox);
        editRequestPanel.add(startDateLabel);
        editRequestPanel.add(startDateTextField);
        editRequestPanel.add(endDateLabel);
        editRequestPanel.add(endDateTextField);
        editRequestPanel.add(saveButton);

        saveButton.addActionListener(e -> {
            String newReason = (String) reasonComboBox.getSelectedItem();
            String newStartDate = startDateTextField.getText();
            String newEndDate = endDateTextField.getText();

            if (newReason == null || newStartDate.isEmpty() || newEndDate.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Svi podaci moraju biti uneseni.", "Greška", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Date startDate = validateDate(newStartDate);
            Date endDate = validateDate(newEndDate);

            if (startDate == null || endDate == null) {
                JOptionPane.showMessageDialog(null, "Datum nije ispravan. Format mora biti yyyy-MM-dd.", "Greška", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Request updatedRequest = new Request(newReason, oldApprovedStatus, startDate, endDate);
            updatedRequest.setId(requestId);

            employeeModel.updateRequest(updatedRequest.getId(), updatedRequest.getReason(),
                    new SimpleDateFormat("yyyy-MM-dd").format(updatedRequest.getStartDate()),
                    new SimpleDateFormat("yyyy-MM-dd").format(updatedRequest.getEndDate()),
                    updatedRequest.getApproved());
            employeeForm.populateRequestTable(employeeModel.getRequests());
            JOptionPane.showMessageDialog(null, "Zahtjev uspješno ažuriran!", "Informacija", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private Date validateDate(String dateText) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(dateText);
        } catch (Exception e) {
            return null;
        }
    }

    public JPanel getEditRequestPanel() {
        return editRequestPanel;
    }
}
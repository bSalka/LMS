import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddRequestForm {
    private JPanel addRequestPanel;
    private JComboBox<String> reasonComboBox;
    private JLabel reasonLabel;
    private JLabel startDateRequestLabel;
    private JLabel endDateRequestLabel;
    private JTextField startDayTextField;
    private JTextField endDayTextField;
    private JButton submitRequestButton;
    private JLabel requestIDLabel;

    private EmployeeModel employeeModel;
    private EmployeeForm employeeForm;

    public AddRequestForm(EmployeeModel employeeModel, EmployeeForm employeeForm) {
        this.employeeModel = employeeModel;
        this.employeeForm = employeeForm;

        // Popuni razloge
        reasonComboBox.addItem("zdravstveni razlog");
        reasonComboBox.addItem("praznik");
        reasonComboBox.addItem("godišnji odmor");

        submitRequestButton.addActionListener(e -> {
            try {

                String reason = (String) reasonComboBox.getSelectedItem();
                if (reason == null) {
                    JOptionPane.showMessageDialog(null, "Morate odabrati razlog.", "Greška", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String startDateText = startDayTextField.getText();
                String endDateText = endDayTextField.getText();


                Date startDate = validateDate(startDateText);
                Date endDate = validateDate(endDateText);

                if (startDate == null || endDate == null) {
                    JOptionPane.showMessageDialog(null, "Datum nije ispravan. Format mora biti yyyy-MM-dd.", "Greška", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Request newRequest = new Request(reason, "čekanje", startDate, endDate);

                employeeModel.getRequests().add(newRequest);

                String employeeId = employeeModel.getEmployeeId(); // Povezivanje sa zaposlenim
                MongoDBController.getInstance().addRequest(newRequest, employeeId);

                employeeForm.populateRequestTable(employeeModel.getRequests());  // Ovdje ažurirate tabelu

                JOptionPane.showMessageDialog(null, "Zahtjev uspješno dodan!", "Informacija", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Greška prilikom unosa zahtjeva.", "Greška", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
    }

    private Date validateDate(String dateText) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(dateText);
        } catch (Exception e) {
            return null;
        }
    }

    public JPanel getAddRequestPanel() {
        return addRequestPanel;
    }
}
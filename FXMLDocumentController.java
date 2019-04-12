/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package displayqueryresults;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;


import javafx.embed.swing.SwingNode;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowFilter;           
import javax.swing.table.TableModel;    
import javax.swing.table.TableRowSorter;
import java.sql.SQLException;
import java.util.regex.PatternSyntaxException;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * FXML Controller class
 *
 * @author tbj5108
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private BorderPane borderPane;
    @FXML
    private TextArea queryTextArea;
    @FXML
    private TextArea filterTextField;
    @FXML
    private DatePicker FDPicker;
    @FXML
    private DatePicker TDPicker;
    @FXML
    private TextField PQText;
    @FXML
    private TextField LNQText;
    
    // database URL, username and password
    private static final String DATABASE_URL = "jdbc:derby://localhost:1527/pizzadb";
    private static final String USERNAME = "app";
    private static final String PASSWORD = "app";
   
    // default query retrieves all data from Orders table
    private static final String DEFAULT_QUERY = "SELECT * FROM orders";
   
    // used for configuring JTable to display and sort data
    private ResultSetTableModel tableModel;
    private TableRowSorter<TableModel> sorter;
    
    //Q1:   query â€œnumber of orders and income per dayâ€� 
    private static final String NUM_ORDERS_PRICE_PER_DAY_QUERY
           = "select date(ordertime) as Order_Day, count(*) as Order_Number, sum(totalprice) as Order_Total \n"
           + "from orders \n"            
           + "group by date(ordertime) \n"            
           + "order by order_day desc";

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //To Set Up the DEFAULT_QUERY
        queryTextArea.setText(DEFAULT_QUERY);
        // create ResultSetTableModel and display database table
        try {
         // create TableModel for results of DEFAULT_QUERY
         tableModel = new ResultSetTableModel(DATABASE_URL,            
         USERNAME, PASSWORD, DEFAULT_QUERY);                        
         
         // create JTable based on the tableModel    
         JTable resultTable = new JTable(tableModel);

         // set up row sorting for JTable
         sorter = new TableRowSorter<>(tableModel);
         resultTable.setRowSorter(sorter);             

         // configure SwingNode to display JTable, then add to borderPane
         SwingNode swingNode = new SwingNode();
         swingNode.setContent(new JScrollPane(resultTable));
         borderPane.setCenter(swingNode);
      }
      catch (SQLException sqlException) {
         displayAlert(AlertType.ERROR, "Database Error", 
            sqlException.getMessage());
         tableModel.disconnectFromDatabase(); // close connection  
         System.exit(1); // terminate application
      } 
    }    
    
    
    @FXML
    private void submitQueryButtonPressed(ActionEvent event) {
        // perform a new query
      try {
         tableModel.setQuery(queryTextArea.getText());
      } 
      catch (SQLException sqlException) {
         displayAlert(AlertType.ERROR, "Database Error", 
            sqlException.getMessage());
         
         // try to recover from invalid user query 
         // by executing default query
         try {
            tableModel.setQuery(DEFAULT_QUERY);
            queryTextArea.setText(DEFAULT_QUERY);
         } 
         catch (SQLException sqlException2) {
            displayAlert(AlertType.ERROR, "Database Error", 
               sqlException2.getMessage());
            tableModel.disconnectFromDatabase(); // close connection  
            System.exit(1); // terminate application
         } 
      } 
    }
    
    
    @FXML
    private void applyFilterButtonPressed(ActionEvent event) {
        String text = filterTextField.getText();

        if (text.length() == 0) {
            sorter.setRowFilter(null);
        }
        else {
            try {
                sorter.setRowFilter(RowFilter.regexFilter(text));
            } 
            catch (PatternSyntaxException pse) {
                displayAlert(AlertType.ERROR, "Regex Error", "Bad regex pattern");
         }
      }
    }
    
      // display an Alert dialog
    private void displayAlert(
        AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    //Q1
    @FXML
    private void simpleQueriesButtonPressed(ActionEvent event) {
                try{
            tableModel.setQuery(NUM_ORDERS_PRICE_PER_DAY_QUERY);
        }
        catch(SQLException sqlException){
            displayAlert(AlertType.ERROR, "DatabaseError", sqlException.getMessage());
            
            // try to recover from invalid user query 
            // by executing default query
            try{
                tableModel.setQuery(DEFAULT_QUERY);
                queryTextArea.setText(DEFAULT_QUERY);
            }
            catch(SQLException sqlException2){
                displayAlert(AlertType.ERROR, "Database Error",
                sqlException2.getMessage());
                tableModel.disconnectFromDatabase();
                //disconnect from database
                System.exit(1); //terminate application
            }
        }
    }

    //Q2
    @FXML
    private void RangeQueriesButtonPressed(ActionEvent event) {
                String searchString
                = "SELECT * FROM orders"
                + " where date(OrderTime) >= '" 
                + FDPicker.getValue()
                + "' AND date(OrderTime) <= "
                + "'" + TDPicker.getValue() + "'";
        try {
            tableModel.setQuery(searchString);
        } 
        catch (SQLException sqlException) {
            displayAlert(AlertType.ERROR, "Database Error",
                    sqlException.getMessage());
            //try to recover from invalid user query
            // by executing default query
            try{
                tableModel.setQuery(DEFAULT_QUERY);
                queryTextArea.setText(DEFAULT_QUERY);
            }
            catch(SQLException sqlException2){
                displayAlert(AlertType.ERROR, "Database Error",
                        sqlException2.getMessage());
                tableModel.disconnectFromDatabase(); // disconnected from database
                System.exit(1);//terminate application  
            }
        }
    }
    
    //Q3
    @FXML
    private void QuerybyPhoneButtonPressed(ActionEvent event) {
        String ORDER_SEARCH_BY
                = "select firstname, lastname, ordertime, pizza.pizzades, ordereditems.pizzaqn, sides.sidesdes, ordereditems.sidesqn, drinks.drinkdes, ordereditems.drinkqn\n"
                + "from customers\n"
                + "inner join orders on customers.phonenumber = orders.phonenumber\n"
                + "inner join ordereditems on ordereditems.orderid = orders.orderid\n"
                + "inner join pizza on pizza.pizzaid = ordereditems.pizzaid\n"
                + "inner join sides on sides.sidesid=ordereditems.sidesid\n"
                + "inner join drinks on drinks.drinkid=ordereditems.drinkid\n"
                + "where customers.phonenumber = '" + PQText.getText() + "'";//get text from phonenumber text field.
        try {
            tableModel.setQuery(ORDER_SEARCH_BY);//query phonenumber
        } 
        catch (SQLException sqlException) {
            displayAlert(AlertType.ERROR, "Database Error", sqlException.getMessage());
            // try to recover from invalid user query 
            // by executing default query
            try {
                tableModel.setQuery(DEFAULT_QUERY);
                queryTextArea.setText(DEFAULT_QUERY);
            } catch (SQLException sqlException2) {
                displayAlert(AlertType.ERROR, "Database Error", sqlException2.getMessage());
                tableModel.disconnectFromDatabase(); // disconnected from database 
                System.exit(1); // terminate application
            }
        }
    }

    //Q4
    @FXML
    private void QuerybyLastnameButtonPressed(ActionEvent event) {
        String ORDER_SEARCH_BY
                = "select firstname, lastname, ordertime, pizza.pizzades, ordereditems.pizzaqn, sides.sidesdes, ordereditems.sidesqn, drinks.drinkdes, ordereditems.drinkqn\n"
                + "from customers\n"
                + "inner join orders on customers.phonenumber = orders.phonenumber\n"
                + "inner join ordereditems on ordereditems.orderid = orders.orderid\n"
                + "inner join pizza on pizza.pizzaid = ordereditems.pizzaid\n"
                + "inner join sides on sides.sidesid=ordereditems.sidesid\n"
                + "inner join drinks on drinks.drinkid=ordereditems.drinkid\n"
                + "where customers.lastname = '" + LNQText.getText() + "'";//get text from last name text field
        try {
            tableModel.setQuery(ORDER_SEARCH_BY);//query lastname. 
        } 
        catch (SQLException sqlException) {
            displayAlert(AlertType.ERROR, "Database Error",
                    sqlException.getMessage());

            // try to recover from invalid user query 
            // by executing default query
            try {
                tableModel.setQuery(DEFAULT_QUERY);
                queryTextArea.setText(DEFAULT_QUERY);
            } catch (SQLException sqlException2) {
                displayAlert(AlertType.ERROR, "Database Error",
                        sqlException2.getMessage());
                tableModel.disconnectFromDatabase(); // disconnected from database 
                System.exit(1); // terminate application
            }
        }
    }  
}

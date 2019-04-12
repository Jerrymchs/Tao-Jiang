/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package displayqueryresults;

/**
 *
 * @author tbj5108
 */
// Fig. 24.29: JdbcRowSetTest.java
// Displaying the contents of the Authors table using JdbcRowSet.
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import javax.sql.rowset.JdbcRowSet;    
import javax.sql.rowset.RowSetProvider;

public class JdbcRowSetTest {
   // JDBC driver name and database URL                              
   private static final String DATABASE_URL = "jdbc:derby://localhost:1527/pizzadb";
   private static final String USERNAME = "app";
   private static final String PASSWORD = "app";
   
   public static void main(String args[]) {
      // connect to database books and query database
      try (JdbcRowSet rowSet =                          
         RowSetProvider.newFactory().createJdbcRowSet()) {

         // specify JdbcRowSet properties 
         rowSet.setUrl(DATABASE_URL);                            
         rowSet.setUsername(USERNAME);                           
         rowSet.setPassword(PASSWORD);                           
         rowSet.setCommand("SELECT * FROM ORDERS"); // set query
         rowSet.execute(); // execute query                      

         // process query results
         ResultSetMetaData metaData = rowSet.getMetaData();
         int numberOfColumns = metaData.getColumnCount();
         System.out.printf("Authors Table of Books Database:%n%n");

         // display rowset header
         for (int i = 1; i <= numberOfColumns; i++) {
            System.out.printf("%-8s\t", metaData.getColumnName(i));
         }
         System.out.println();
         
         // display each row
         while (rowSet.next()) {
            for (int i = 1; i <= numberOfColumns; i++) {
               System.out.printf("%-8s\t", rowSet.getObject(i));
            }
            System.out.println();
         } 
      }
      catch (SQLException sqlException) {
         sqlException.printStackTrace();
         System.exit(1);
      } 
   } 
}


/**************************************************************************
 * (C) Copyright 1992-2018  by Deitel & Associates, Inc. and               *
 * Pearson Education, Inc. All Rights Reserved.                           *
 *                                                                        *
 * DISCLAIMER: The authors and publisher of this book have used their     *
 * best efforts in preparing the book. These efforts include the          *
 * development, research, and testing of the theories and programs        *
 * to determine their effectiveness. The authors and publisher make       *
 * no warranty of any kind, expressed or implied, with regard to these    *
 * programs or to the documentation contained in these books. The authors *
 * and publisher shall not be liable in any event for incidental or       *
 * consequential damages in connection with, or arising out of, the       *
 * furnishing, performance, or use of these programs.                     *
 *************************************************************************/

package comp3111.covid;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
/**
 * @author siuka
 * reference: https://www.codejava.net/java-se/swing/a-simple-jtable-example-for-display
 * https://www.zentut.com/java-swing/how-to-use-jtable-to-display-data/
 */

public class Table extends JFrame
{
    public Table()
    {
    	
        //headers for the table, table column (need replaced)
        String[] columns = new String[] {
            "Id", "Name", "Hourly Rate", "Part Time"
        };
         
        //actual data for the table in a 2d array
        // 2D array for data [row][column], todo is may make it dynamic and get data from database (need replaced)
        Object[][] data = new Object[][] {
            {1, "John", 40.0, false },
            {2, "Rambo", 70.0, false },
            {3, "Zorro", 60.0, true },
        };
         
        final Class[] columnClass = new Class[] { // datatype of column?
            Integer.class, String.class, Double.class, Boolean.class
        };
        //create table model with data
        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) // cell not editable
            {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) /// for getting class of column?
            {
                return columnClass[columnIndex];
            }
        };


        JTable table = new JTable(model); // make table (with model)
        JScrollPane scrollPane = new JScrollPane(table); // make scrollbars
        table.setFillsViewportHeight(true); // always make it large enough to fullfill height of enclosing viewpoint

        JLabel lblHeading = new JLabel("Stock Quotes"); // title (need replaced)
        lblHeading.setFont(new Font("Arial",Font.TRUETYPE_FONT,24)); // fonttype used for title

        this.getContentPane().setLayout(new BorderLayout()); // make new layout manager for layout setting below
        this.getContentPane().add(lblHeading,BorderLayout.PAGE_START); // heading in start of page
        this.getContentPane().add(scrollPane,BorderLayout.CENTER);  // scrollpane is in center

        this.setTitle("Table Example"); //  windows title 
        this.pack(); // sizes the frame (window size) so that all its contents are at or above their preferred sizes
        this.setVisible(true); // show window
    }
}
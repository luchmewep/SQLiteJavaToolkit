import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class SqliteToolkit {
	private String db_url;
	private Connection con;
	private PreparedStatement pst;
	private ResultSetMetaData rsmd;
	@SuppressWarnings("rawtypes")
	private Vector tblHeader, columnData, rowData;
	@SuppressWarnings("rawtypes")
	private Vector<Vector> tblRows;

	/**
	 * <p>Provide value for <em>db_url</em>.<br>
	 * Example of db_url: <b>C:\Users\jsluc\Desktop\students.db</b></p>
	 * @param db_url
	 */
	public SqliteToolkit(String db_url) {
		this.db_url = db_url;
	}

	/**
	 * <p>Method to Test Connection</p>
	 * @throws SQLException 
	 */
	public void testConnection() {
		openDatabase();
		System.out.println("Connection success!"); //if no problem with database connection
		closeDatabase();
	}

	/**
	 * Returns PreparedStament (does not require binder)
	 * @param sql (String)
	 * @return PreparedStatement
	 */
	public PreparedStatement getPST(String sql) {
		return getPST(sql, null);
	}

	/**
	 * Returns PreparedStatement (binder required)
	 * @param sql (String)
	 * @param binder (Object-type Vector)
	 * @return PreparedStatement
	 */
	@SuppressWarnings("rawtypes")
	public PreparedStatement getPST(String sql, Vector binder) {
		openDatabase();
		try {
			pst = con.prepareStatement(sql);
			if(binder != null) {
				for (int i = 0; i < binder.size(); i++) {
					pst.setObject(i+1, binder.get(i));
				}
			}
			return pst;
		} catch (Exception e) {
			System.err.println("Error @getPST");
			return null;
		}
	}

	/**
	 * Returns true for success or false for fail from PreparedStatement
	 * @param pst
	 * @return boolean
	 */
	public boolean executePST(PreparedStatement pst) {
		try {
			if(pst.executeUpdate() == 1) {
				return true;
			}
			else return false;
		} catch (Exception e) {
			System.err.println("Error @executePST: "+e.getMessage());
			return false;
		}
	}
	
	/**
	 * Returns ResultSet from PreparedStatement
	 * @param pst (PreparedStatement)
	 * @return ResultSet
	 */
	public ResultSet getRS(PreparedStatement pst) {
		try {
			return pst.executeQuery();
		} catch (Exception e) {
			System.err.println("Error @getRS: "+e.getMessage());
			return null;
		}
	}

	/**
	 * Returns one row from the ResultSet
	 * @param rs (ResultSet)
	 * @return Vector (Object-type)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getRow(ResultSet rs) {
		try {
			rowData = new Vector<>();
			int columns = rs.getMetaData().getColumnCount();
			while(rs.next()) {
				for (int i = 1; i <= columns; i++) {
					rowData.add(rs.getObject(i));
				}
				break;
			}
			return rowData;
		} catch (Exception e) {
			System.err.println("Error @getRow: "+e.getMessage());
			return null;
		}
	}

	/**
	 * Returns one column from the ResultSet
	 * @param rs (ResultSet)
	 * @param columnName (String)
	 * @return Vector (Object-type)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" } )
	public Vector getColumn(ResultSet rs, String columnName) {
		try {
			columnData = new Vector<>();
			while(rs.next()) {
				columnData.add(rs.getObject(columnName));
			}
			return columnData;
		} catch (Exception e) {
			System.err.println("Error @getColumn: "+e.getMessage());
			return null;
		}
	}

	/**
	 * Returns column names from the ResultSet
	 * @param rs (ResultSet)
	 * @return Vector (Object-type)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Vector getColumnNames(ResultSet rs){
		try {
			tblHeader = new Vector<>();
			rsmd = rs.getMetaData();
			int columns = rsmd.getColumnCount();
			for (int i = 1; i <= columns; i++) {
				tblHeader.add(rsmd.getColumnName(i));	
			}
			return tblHeader;
		} catch (Exception e) {
			System.err.println("Error @getColumnNames: "+e.getMessage());
			return null;
		}
	}

	/**
	 * Returns column labels from the ResultSet
	 * @param rs (ResultSet)
	 * @return Vector (Object-type)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Vector getColumnLabels(ResultSet rs){
		try {
			tblHeader = new Vector<>();
			rsmd = rs.getMetaData();
			int columns = rsmd.getColumnCount();
			for (int i = 1; i <= columns; i++) {
				tblHeader.add(rsmd.getColumnLabel(i));				
			}
			return tblHeader;
		} catch (Exception e) {
			System.err.println("Error @getColumnLabels: "+e.getMessage());
			return null;
		}
	}

	/**
	 * Returns all rows from the ResultSet
	 * @param rs (ResultSet)
	 * @return 2D Vector (Object-type)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Vector<Vector> getRows(ResultSet rs){
		try {
			tblRows = new Vector<>();
			int columns = rs.getMetaData().getColumnCount();
			int row = 0;
			while (rs.next()) {
				tblRows.add(new Vector<>());
				for (int i = 1; i <= columns; i++) {
					tblRows.get(row).add(rs.getObject(i));
				}
				row++;
			}
			return tblRows;
		} catch (Exception e) {
			System.err.println("Error @getRows: "+e.getMessage());
			return null;
		}
	}

	/**
	 * Returns JTable model from a ResultSet
	 * @param rs (ResultSet)
	 * @return DefaultTableModel for JTable
	 */
	public DefaultTableModel getTableModel(ResultSet rs) {
		//Instantiate Vectors
		tblHeader = new Vector<>();
		tblRows = new Vector<>();

		//Get Column Names
		tblHeader = getColumnLabels(rs);

		//Get Rows from Result Set
		tblRows = getRows(rs);

		return new DefaultTableModel(tblRows, tblHeader);
	}

	/**
	 * Returns JComboBox model from an Object-type Vector
	 * @param v (Object-type Vector)
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public DefaultComboBoxModel getComboBoxModel(Vector v) {
		return new DefaultComboBoxModel<>(v);
	}
	
	//OPEN and CLOSE database
	private void openDatabase() {
		try {
			con = DriverManager.getConnection("jdbc:sqlite:"+db_url);
		} catch (SQLException e) {
			System.err.println("Error @openDatabase: "+e);
		}
	}
	private void closeDatabase() {
		try {
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			System.err.println("Error @closeDatabase: "+e);
		}
	}
}
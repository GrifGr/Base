package greg;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.table.AbstractTableModel;

public class MyTableModel extends AbstractTableModel {
	//private String[] columnNames = null;
	//private Object[][] data = null;

	private ArrayList<String> columnNames = null;
	private ArrayList<ArrayList<Object>> data = null;
	
	public MyTableModel() {
		columnNames = new ArrayList<String>();
		data = new ArrayList<ArrayList<Object>>();
		//this.columnNames = new String[0];
		// this.names = new Vector<String>();
		// this.rows = new Vector<Vector<Object>>();
	}

	@Override
	public int getRowCount() {
//		int romsize = 0;
//		try {
//			romsize = data.size();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		return data.size();
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return columnNames.size();
	}

	public String getColumnName(int col) {
		return columnNames.get(col);
	}


	public void setData(ResultSet rs) {	
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdfdbf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			ResultSetMetaData rsMetaData = rs.getMetaData();
			int columnCount = rsMetaData.getColumnCount();

			columnNames = new ArrayList<String>();

			for (int i = 1; i <= columnCount; i++) {
				columnNames.add(rsMetaData.getColumnName(i));
			}
			
//			rs.last();
//			int countRows = rs.getRow();
//			rs.first();
			
			data = new ArrayList<ArrayList<Object>>();
			
			while (rs.next()) {
				//String gg = rs.getString(8);
				String newString = "";
//				try {
//					//newString = new String(rs.getString(8).getBytes("Cp1251"), "Cp866");
//				} catch (UnsupportedEncodingException e2) {
//					// TODO Auto-generated catch block
//					e2.printStackTrace();
//				}
				//System.out.println(rs.getObject(8).toString());
				ArrayList<Object> newRow = new ArrayList<Object>(columnNames.size());

				for (int i = 1; i <= columnCount; i++) {
					//System.out.println(rs.getObject(i));
					int classname = rsMetaData.getColumnType(i);
					switch (classname) {
					case Types.DOUBLE:
					case Types.NUMERIC:
					case Types.INTEGER:
						double dd = 0;
						try {
							//Object ff = rs.getObject(i);
							dd = (Double) rs.getObject(i);
						} catch (Exception e) {
						}
						
						//newRow.add(String.valueOf(dd));
						newRow.add(dd);
						break;
					case Types.DATE:
					case Types.TIME:
					case Types.TIMESTAMP:
						boolean error = false;
						// JOptionPane.showMessageDialog(null, rs.getObject(i));

						Calendar DatCor = GregorianCalendar.getInstance();
						try {
							DatCor.setTime(sdfdbf.parse(rs.getObject(i).toString()));
						} catch (Exception e1) {
							error = true;
							DatCor.clear();
						}

						if (error) {
							newRow.add("");
						} else
							newRow.add(sdf.format(DatCor.getTime()).toString());
						break;
					default:
						newRow.add(rs.getObject(i));
						break;
					}
				}
				data.add(newRow);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		return data.get(rowIndex).get(columnIndex);
	}

}

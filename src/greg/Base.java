package greg;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class Base extends JFrame {
	private static final long serialVersionUID = 1L;
	private JFileChooser fileChooser = null;
	private final String[][] FILTERS = { { "DBF", "dBase (*.dbf)" } };
	private JTable table = null;
	private MyTableModel tableModel = null;
	private PropertyReader prop = new PropertyReader();
	private JMenu fileMenu = null;
			
	public Base(String path) {
		// JFrame frame = new JFrame("DBF");
		super("DBF");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(screenSize.width / 2, screenSize.height / 2);
		// setSize(350, 200);

		// Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width - getWidth()) / 2, (screenSize.height - getHeight()) / 3);

		setJMenuBar(addMenuBar());

		tableModel = new MyTableModel();

		table = new JTable(tableModel);

		// table.setFillsViewportHeight(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		// table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		JScrollPane scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);

		add(scrollPane);

		if (path != "") {
			File file = new File(path);
			if (file.exists()) {
				connect(file);
			}
		}
		setVisible(true);

	}

	private JMenuBar addMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		fileMenu = new JMenu("Файл");
		menuBar.add(fileMenu);

	    fileMenu.addMenuListener(new MenuListener() {

	        public void menuSelected(MenuEvent e) {
	          //System.out.println("menuSelected");
	          addFileMenu();
	        }

	        public void menuDeselected(MenuEvent e) {
	          //System.out.println("menuDeselected");
	        }

	        public void menuCanceled(MenuEvent e) {
	          //System.out.println("menuCanceled");
	        }
	      });	    
	    
		return menuBar;
	}

	private void addFileMenu(){
		fileMenu.removeAll();
		
		JMenuItem open = new JMenuItem("Открыть");
		JMenuItem exit = new JMenuItem("Выход");
	    
		fileMenu.add(open);
		fileMenu.addSeparator();

		String[] listFile = prop.getListFile();
		for (int i = listFile.length-1; i >= 0; i--) {
		//for (final String fileOpen : listFile) {
			//final JMenuItem menuOpenFile = new JMenuItem(fileOpen);
			final JMenuItem menuOpenFile = new JMenuItem(listFile[i]);
			menuOpenFile.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					connect(new File(menuOpenFile.getText()));
				}
			});
			fileMenu.add(menuOpenFile);
		}

		fileMenu.addSeparator();
		fileMenu.add(exit);

		open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Выбор файла");
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.setAcceptAllFileFilterUsed(false);

				FileFilter filter = new FileNameExtensionFilter("dBase (*.dbf)", "dbf");
				fileChooser.addChoosableFileFilter(filter);

				int result = fileChooser.showOpenDialog(Base.this);
				if (result == JFileChooser.APPROVE_OPTION) {
					connect(fileChooser.getSelectedFile());
				};
			}
		});

		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
	}
	
	
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				String path = "";
				if (args.length > 0) {
					path = new String(args[0]);
				}
				new Base(path);
			}

		});

	}

	private void connect(File FileBase) {
		Connection connection;
		ResultSet resultSet = null;
		Statement stmt;

		try {
			// Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			// String connString = "jdbc:odbc:Driver={Microsoft Access dBASE
			// Driver (*.dbf, *.ndx, *.mdx)};DefaultDir=K:/CHASTNT";
			String connString = "jdbc:odbc:Driver={Microsoft dBASE Driver (*.dbf)};DefaultDir=" + FileBase.getParent();

			Properties connInfo = new Properties();
			// onnInfo.put("charSet", "Cp1251");
			// connInfo.put("charSet", "Cp866");
			// connInfo.put("CODEPAGEID", "66");

			connection = DriverManager.getConnection(connString, connInfo);
			stmt = connection.createStatement();
			String sql = "Select * from [" + FileBase.getName() + "]";
			PreparedStatement p = connection.prepareStatement(sql);
			resultSet = p.executeQuery();

			tableModel.setData(resultSet);

			tableModel.fireTableStructureChanged();

			prop.setListFile(FileBase.getAbsolutePath());
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}
}

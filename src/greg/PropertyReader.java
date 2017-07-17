package greg;


import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Properties; 

public class PropertyReader {
    private static final String sFileName = "Base.properties";
    private static String sDirSeparator = System.getProperty("file.separator");
    private static Properties props = new Properties(); 
    private boolean fileExists = false;
    private int countList = 5;
    private String sFilePath = "";
    String[] listFile = null;
    ArrayList<String> arrayListFile = null;
    
    {
    	// определ€ем текущий каталог
        File currentDir = new File(".");  
        
        try {
            // определ€ем полный путь к файлу
            sFilePath = currentDir.getCanonicalPath() + sDirSeparator + sFileName; 

            File fileProperties = new File(sFilePath);
            if (!fileProperties.exists()){
                try
                {
                	fileExists = fileProperties.createNewFile();
                }
                catch(IOException ex){                     
                    System.out.println("Ќе могу создать файл настроек.");;
                }  
            } else{
            	fileExists = true;
            }
            
            // создаем поток дл€ чтени€ из файла
            FileInputStream ins = new FileInputStream(sFilePath); 

            // загружаем свойства
            props.load(ins);

            // выводим значение дл€ свойства mykey
            //System.out.println(props.getProperty("mykey"));
        }

        catch (FileNotFoundException e) {
            System.out.println("File not found!");
            e.printStackTrace();
        }

        catch (IOException e) {
            System.out.println("IO Error!");
            e.printStackTrace();
        }        
    }
    
    public String getProperty(String property) {
		return props.getProperty(property);
	}
    
    public void setProperty(String property, String value) {
		props.setProperty(property, value);
		
		try {
			props.store(new FileOutputStream(sFilePath), null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    public String[] getListFile() {
    	String prop = getProperty("countFileOpen");
    	if (!(prop == null || prop == "")) {
    		countList = Integer.parseInt(prop);
		}
    	
    	arrayListFile = new ArrayList<String>();
    	
    	for (int i = 0; i < countList; i++) {
    		String fileOpen = getProperty("FileOpen" + i);
        	if (!(fileOpen == null || fileOpen == "")) {
        		arrayListFile.add(fileOpen);
    		}
		}
    	
    	listFile = arrayListFile.toArray(new String[arrayListFile.size()]);
    	
    	return listFile;
    }
    
    public void setListFile(String fileOpen) {
    	if (arrayListFile!=null){
    		arrayListFile.remove(fileOpen);
    		arrayListFile.add(fileOpen);
    		
    		if (arrayListFile.size()>countList) {
    			arrayListFile.remove(0);
			}
    		
    		for (int i = 0; i < arrayListFile.size(); i++) {
    			setProperty("FileOpen" + i, arrayListFile.get(i));
			}    	
		}
	}
};



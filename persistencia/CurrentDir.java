package persistencia;

import java.io.File;

/**
 * Clase para el manejo del directorio actual
 * @author andres
 */

public class CurrentDir {
	/**
	 * 
	 * @return string con el directorio actual
	 */
	public static String getCurrentDir() {
		File dir1 = new File (".");
		String result = "";
		try {
			result = dir1.getCanonicalPath();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
    }
}
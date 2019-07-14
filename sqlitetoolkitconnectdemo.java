
public class sqlitetoolkitconnectdemo {

	public static void main(String[] args) {
		SqliteToolkit db = new SqliteToolkit("C:\\Users\\jsluc\\Desktop\\students.db");
		db.testConnection();
	}

}

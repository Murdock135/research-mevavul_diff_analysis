class execute_1 {
public static void execute() {

		String create = "CREATE TABLE user (name VARCHAR(50) NOT NULL, "
				+ "password VARCHAR(100) NOT NULL, "
				+ "creditCardNumber VARCHAR(12) NOT NULL)";
		Database.execute(create);

		insertUser("Guilherme", "EuAmoGatinhos", "123456789012");
		insertUser("Bruno", "LaFooon", "187456779012");
		insertUser("Jefferson", "DaniboySZ", "487456789012");
		insertUser("Leonardo", "MinoZica2014", "587456789112");
		insertUser("Renato", "Paulas2", "787456789112");

	}
}

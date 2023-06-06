/*
 * @Author: Matilde Pato (mpato)
 * @Date:   2023-05-26 18:55:00
 * @Last Modified time: 2023-05-26 18:55:00
 * ISEL - DEETC
 * Introdução a Sistemas de Informação
 * MPato, 2022-2023
 *
 * NOTA: O código encontra-se dividido por classes, não deve adicionar mais classes,
 * nem alterar a sua configuração inicial.
 * 1) a classe PrincipalInspection é uma classe que contém os atributos da tabela
 * INSPECCAOPRINCIPAL. Já está implementada!
 * 2) a classe Model é onde deverão implementar todos os métodos da aplicação
 * 3) a classe Restriction deve conter as restrições ao modelo de dados. Ela
 * é executada, apenas, quando existe uma nova entrada nas tabelas a que está
 * afecta.
 *
 */
package jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashMap;
interface DbWorker
{
    void doWork();
}
class App {
    private final String URL = "jdbc:postgresql://10.62.73.58:5432/";
    private final String USER_NAME = "ab12";
    private final String PASSWORD = "ab12";

    private enum Option {
        // DO NOT CHANGE ANYTHING!
        Unknown,
        Exit,
        novelInspection,
        removeInspector,
        totalCost,
        listWorks,
        listContentionWorks,
        listInspectors,
    }

    private static App __instance = null;
    private String __connectionString;

    private HashMap<Option, DbWorker> __dbMethods;

    private App() {
        // DO NOT CHANGE ANYTHING!
        __dbMethods = new HashMap<Option, DbWorker>();
        __dbMethods.put(Option.novelInspection, () -> App.this.novelInspection());
        __dbMethods.put(Option.removeInspector, () -> App.this.removeInspector());
        __dbMethods.put(Option.totalCost, () -> App.this.totalCost());
        __dbMethods.put(Option.listWorks, () -> App.this.listWorks());
        __dbMethods.put(Option.listContentionWorks, new DbWorker() {
            public void doWork() {
                App.this.listContentionWorks();
            }
        });
        __dbMethods.put(Option.listInspectors, new DbWorker() {
            public void doWork() {
                App.this.listInspectors();
            }
        });
    }

    public static App getInstance() {
        if (__instance == null) {
            __instance = new App();
        }
        return __instance;
    }

    private Option DisplayMenu() {
        Option option = Option.Unknown;
        try {
            // DO NOT CHANGE ANYTHING!
            System.out.println("Company management");
            System.out.println();
            System.out.println("1. Exit");
            System.out.println("2. Novel inspection"); //1
            System.out.println("3. Remove inspector"); //2
            System.out.println("4. Cost total"); //2.c
            System.out.println("5. List works"); //2.g
            System.out.println("6. List contention works"); //3.d
            System.out.println("7. List inspector"); //3.e
            System.out.print(">");
            Scanner s = new Scanner(System.in);
            int result = s.nextInt();
            option = Option.values()[result];
        } catch (RuntimeException ex) {
            //nothing to do.
        }
        return option;

    }

    private static void clearConsole() throws Exception {
        for (int y = 0; y < 25; y++) //console is 80 columns and 25 lines
            System.out.println("\n");

    }

    private void Login() throws java.sql.SQLException {
        Connection con = DriverManager.getConnection(getConnectionString());
        if (con != null)
            con.close();
    }

    public void Run() throws Exception {
        Login();
        Option userInput;
        do {
            clearConsole();
            userInput = DisplayMenu();
            clearConsole();
            try {
                __dbMethods.get(userInput).doWork();
                System.in.read();

            } catch (NullPointerException ex) {
                //Nothing to do. The option was not a valid one. Read another.
            }

        } while (userInput != Option.Exit);
    }

    public String getConnectionString() {
        return __connectionString;
    }

    public void setConnectionString(String s) {
        __connectionString = s;
    }

    /**
     * To implement from this point forward. Do not need to change the code above.
     * -------------------------------------------------------------------------------
     * IMPORTANT:
     * --- DO NOT MOVE IN THE CODE ABOVE. JUST HAVE TO IMPLEMENT THE METHODS BELOW ---
     * -------------------------------------------------------------------------------
     */

    private static final int TAB_SIZE = 24;

    void printResults(ResultSet dr) throws SQLException {

        ResultSetMetaData meta = dr.getMetaData();
        int columnsCount = meta.getColumnCount();
        StringBuffer sep = new StringBuffer("\n");

        // This code is just demonstrative and only works for the two
        // columns existing in the table jdbcdemo
        for (int i = 1; i <= columnsCount; i++) {
            System.out.print(meta.getColumnLabel(i));
            System.out.print("\t");

            for (int j = 0; j < meta.getColumnDisplaySize(i) + TAB_SIZE; j++) {
                sep.append('-');
            }
        }
        System.out.println(sep);
        // Step 4 - Get result
        while (dr.next()) {
            // It's not the best way to do it. But as in this case the result
            // is to be exclusively displayed on the console, the practical
            // result serves
            for (int i = 1; i <= columnsCount; i++) {
                System.out.print(dr.getObject(i));
                System.out.print("\t");
            }
            System.out.println();
        }
        System.out.println();
        /*Result must be similar like:
        ListDepartment()
        dname           dnumber     mgrssn      mgrstartdate            
        -----------------------------------------------------
        Research        5           333445555   1988-05-22            
        Administration  4           987654321   1995-01-01
        */
    }

    private void novelInspection() {
        // IMPLEMENTED
        System.out.println("novelInspection()");
        String values = Model.inputData("work id, condition index and state of conservation.\n");
        // validate all entries!
        PrincipalInspection pi = new PrincipalInspection(values);
        Model.registerInspection(pi);
    }

    private void removeInspector() {
        // TODO
        System.out.println("removeInspector()");

    }

    private void totalCost() {
        System.out.println("totalCost()");
        Connection conn = null;
        PreparedStatement pstmt;
        ResultSet rs = null;
        try {

            conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
            String subQuery = "SELECT id FROM tipo_estrutura WHERE tipo = ? OR tipo = ?";
            String cmdSELECT = "SELECT tipo_estrutura, SUM(custo) AS custo_total " +
                    "FROM OBRA_CONTENCAO " +
                    "WHERE tipo_estrutura IN (" + subQuery + ") " +
                    "GROUP BY tipo_estrutura";

            pstmt = conn.prepareStatement(cmdSELECT);
            Scanner scanner = new Scanner(System.in);
            System.out.print("Tipo de Estrutura 1: (muro, parede, aterro, talude, barreira ou solução mista.)");
            String tipoDeEstrutura1 = scanner.nextLine();
            System.out.print("Tipo de Estrutura 2: (muro, parede, aterro, talude, barreira ou solução mista.)");
            String tipoDeEstrutura2 = scanner.nextLine();
            pstmt.setString(1, tipoDeEstrutura1);
            pstmt.setString(2, tipoDeEstrutura2);
            rs = pstmt.executeQuery();
            printResults(rs);
        }catch (SQLException sqlex) {
            System.out.println(sqlex.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void listWorks() {
        System.out.println("listWorks()");
        Connection conn = null;
        PreparedStatement pstmt;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
            String subQuery1 = "gestor  IN (" +
                    "                    SELECT email" +
                    "                    FROM utilizador" +
                    "                    WHERE NOME = ?" +
                    "            ) or inspetor IN (" +
                    "                    SELECT email" +
                    "                    FROM utilizador" +
                    "                    WHERE NOME = ?" +
                    "            )";
            String subQuery2 = "SELECT email\n" +
                    "            FROM utilizador\n" +
                    "            WHERE NOME = ?";
            String cmdSELECT = "SELECT TRABALHO.id FROM TRABALHO " +
            "INNER JOIN OBRA_CONTENCAO ON TRABALHO.id_obra = OBRA_CONTENCAO.id " +
            "WHERE (" +  subQuery1 + ") AND inspetor not in (" + subQuery2 + ");";

            pstmt = conn.prepareStatement(cmdSELECT);
            Scanner scanner = new Scanner(System.in);
            System.out.print("Nome do Utilizador 1:");
            String user1 = scanner.nextLine();
            System.out.print("Nome do Utilizador 2:");
            String user2 = scanner.nextLine();
            pstmt.setString(1, user1);
            pstmt.setString(2, user1);
            pstmt.setString(3, user2);
            rs = pstmt.executeQuery();
            printResults(rs);
        }catch (SQLException sqlex) {
            System.out.println(sqlex.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void listContentionWorks()
    {
        System.out.println("listWorks()");
        Connection conn = null;
        PreparedStatement pstmt;
        ResultSet rs = null;
        try {

            conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
            String subQuery = "";
            String cmdSELECT = "select * " +
                    "from OBRA_CONTENCAO " +
                    "where " +
                    "id not in ( " +
                    "select id_trabalho " +
                    "from CAMPANHA_MONITORIZACAO " +
                    "inner join TRABALHO on " +
                    "CAMPANHA_MONITORIZACAO.id_trabalho = TRABALHO.id " +
                    "where" +
                    "TRABALHO.estado in ('executado', 'validado') " +
                    ");" ;

                  //  "  WHERE (TRABALHO.estado NOT IN (?, ?) AND TRABALHO.atrdisc = ? );";

            pstmt = conn.prepareStatement(cmdSELECT);
            Scanner scanner = new Scanner(System.in);
            System.out.print("Estado 1: (planeado, executado, ou validado.) ");
            String estado1 = scanner.nextLine();
            System.out.print("Estado 2: (planeado, executado, ou validado.) ");
            String estado2 = scanner.nextLine();
            System.out.print("Tipo de Trabalho: (IP, IR, CM) ");
            String tipoTrabalho = scanner.nextLine();

            //pstmt.setString(1, estado1);
            //pstmt.setString(2, estado2);
            //pstmt.setString(3, tipoTrabalho);

            rs = pstmt.executeQuery();
            printResults(rs);
        }catch (SQLException sqlex) {
            System.out.println(sqlex.getMessage());
        } finally {
            // Step 5 Close connection
            try {
                // free the resources of the ResultSet
                if (rs != null) rs.close();
                // close connection
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void listInspectors()
    {
        // TODO
        System.out.println("listInspectors");
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            // JDBC and PostgreSQL: https://jdbc.postgresql.org/
            // PostgreSQL: https://www.postgresql.org/docs/7.4/jdbc-use.html

            // Step 1 - Load driver
            // Class.forName("org.postgresql.Driver"); // Class.forName() is not needed since JDBC 4.0

            // Step 2 -  Connecting to the Database
            conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD);

            // Step 3 - Execute statement 1
            stmt = conn.createStatement();
            rs = stmt.executeQuery("select * from obra_contencao");
            printResults(rs);
        } catch (SQLException sqlex) {
            System.out.println(sqlex.getMessage());
        } finally {
            // Step 5 Close connection
            try {
                // free the resources of the ResultSet
                if (rs != null) rs.close();
                // free the resources of the Statement
                if (stmt != null) stmt.close();
                // close connection
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}

public class Ap {
    public static void main(String[] args) throws Exception {

        String url = "jdbc:postgresql://10.62.73.58:5432/ab12?user=ab12&password=ab12&ssl=false";
        App.getInstance().setConnectionString(url);
        App.getInstance().Run();
    }
}
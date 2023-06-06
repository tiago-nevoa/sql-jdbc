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

        for (int i = 1; i <= columnsCount; i++) {
            System.out.print(meta.getColumnLabel(i));
            System.out.print("\t");

            for (int j = 0; j < meta.getColumnDisplaySize(i) + TAB_SIZE; j++) {
                sep.append('-');
            }
        }
        System.out.println(sep);
        while (dr.next()) {
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
        Scanner scanner = new Scanner(System.in);
        System.out.print("ID do trabalho/obra de contenção: ");
        int idTrabalho = scanner.nextInt();
        if (idTrabalho < 1 || idTrabalho > 13) {
            System.out.println("ID do trabalho/obra de contenção inválido. Deve estar entre 1 e 13.");
            return;
        }

        System.out.print("Índice de condição (0-100): ");
        int indiceCondicao = scanner.nextInt();
        if (indiceCondicao < 0 || indiceCondicao > 100) {
            System.out.println("Índice de condição inválido. Deve estar entre 0 e 100.");
            return;
        }

        System.out.print("Estado de conservação (1-100): ");
        int estadoConservacao = scanner.nextInt();
        if (estadoConservacao < 1 || estadoConservacao > 100) {
            System.out.println("Estado de conservação inválido. Deve estar entre 1 e 100.");
            return;
        }

        Connection conn;
        PreparedStatement pstmt;
        try {
            conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
            String query = "INSERT INTO INSPECAO_PRINCIPAL (id_trabalho, indice_condicao, estado_conservacao) " +
                    "VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, idTrabalho);
            pstmt.setInt(2, indiceCondicao);
            pstmt.setInt(3, estadoConservacao);
            pstmt.executeUpdate();
            System.out.println("Nova inspeção principal inserida com sucesso!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void removeInspector() {

        System.out.println("removeInspector()");
        Scanner scanner = new Scanner(System.in);
        System.out.print("Email do inspetor a ser removido: ");
        String emailInspetor = scanner.nextLine();

        Connection conn;
        PreparedStatement pstmt;
        try {
            conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD);

            // Atualizar o campo "inspetor" com um novo valor (ou null) para os registros que referenciam o inspetor a ser removido
            String updateQuery = "UPDATE TRABALHO SET inspetor = ? WHERE inspetor = ?";
            pstmt = conn.prepareStatement(updateQuery);
            pstmt.setNull(1, java.sql.Types.VARCHAR); // Definir como null ou fornecer um novo valor de inspetor
            pstmt.setString(2, emailInspetor);
            pstmt.executeUpdate();

            // Remover o inspetor da tabela "UTILIZADOR"
            String deleteQuery = "DELETE FROM UTILIZADOR WHERE email = ?";
            pstmt = conn.prepareStatement(deleteQuery);
            pstmt.setString(1, emailInspetor);

            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Inspetor removido com sucesso!");
            } else {
                System.out.println("Nenhum inspetor encontrado com o email fornecido.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        } catch (SQLException sqlex) {
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
                    "WHERE (" + subQuery1 + ") AND inspetor not in (" + subQuery2 + ");";

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
        } catch (SQLException sqlex) {
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

    private void listContentionWorks() {
        System.out.println("listContentionWorks()");
        Connection conn = null;
        PreparedStatement pstmt;
        ResultSet rs = null;
        try {

            conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD);

            String cmdSELECT = null;
            Scanner scanner = new Scanner(System.in);
            System.out.print("Tipo de Trabalho: (IP, CM)");
            String tipoTrabalho = scanner.nextLine();
            switch (tipoTrabalho) {
                case ("IP"):
                    cmdSELECT = "select * " +
                            "from OBRA_CONTENCAO " +
                            "where " +
                            "id not in ( " +
                            "select id_trabalho " +
                            "from INSPECAO_PRINCIPAL " +
                            "inner join TRABALHO on " +
                            "INSPECAO_PRINCIPAL.id_trabalho = TRABALHO.id " +
                            "where " +
                            "TRABALHO.estado in (? , ?) " +
                            ");";
                    break;
                case ("CM"):
                    cmdSELECT = "select * " +
                            "from OBRA_CONTENCAO " +
                            "where " +
                            "id not in ( " +
                            "select id_trabalho " +
                            "from CAMPANHA_MONITORIZACAO " +
                            "inner join TRABALHO on " +
                            "CAMPANHA_MONITORIZACAO.id_trabalho = TRABALHO.id " +
                            "where " +
                            "TRABALHO.estado in (? , ?) " +
                            ");";
                    break;
            }
            pstmt = conn.prepareStatement(cmdSELECT);
            System.out.print("Estado 1: (planeado, executado ou validado.) ");
            String estado1 = scanner.nextLine();
            System.out.print("Estado 2: (planeado, executado ou validado.) ");
            String estado2 = scanner.nextLine();

            pstmt.setString(1, estado1);
            pstmt.setString(2, estado2);

            rs = pstmt.executeQuery();
            printResults(rs);
        } catch (SQLException sqlex) {
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

    private void listInspectors() {
        // TODO
        System.out.println("listInspectors");
        Connection conn = null;
        PreparedStatement pstmt;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD);

            String cmdSELECT = null;

            cmdSELECT = "SELECT DISTINCT TRABALHO.inspetor " +
                    "FROM TRABALHO " +
                    "INNER JOIN UTILIZADOR ON TRABALHO.inspetor = UTILIZADOR.email " +
                    "WHERE TRABALHO.id_obra IN ( " +
                    "  SELECT id_obra " +
                    "  FROM TRABALHO " +
                    "  INNER JOIN UTILIZADOR ON TRABALHO.gestor = UTILIZADOR.email " +
                    "  WHERE UTILIZADOR.nome = ? )" +
                    "GROUP BY TRABALHO.inspetor " +
                    "HAVING COUNT(DISTINCT TRABALHO.id_obra) = ( " +
                    "  SELECT COUNT(DISTINCT id_obra) " +
                    "  FROM TRABALHO " +
                    "  INNER JOIN UTILIZADOR ON TRABALHO.gestor = UTILIZADOR.email " +
                    "  WHERE UTILIZADOR.nome = ? )";

            pstmt = conn.prepareStatement(cmdSELECT);
            System.out.print("Nome do Gestor: ");
            Scanner scanner = new Scanner(System.in);
            String gestor = scanner.nextLine();
            pstmt.setString(1, gestor);
            pstmt.setString(2, gestor);

            rs = pstmt.executeQuery();
            printResults(rs);
        } catch (SQLException sqlex) {
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
}

public class Ap {
    public static void main(String[] args) throws Exception {

        String url = "jdbc:postgresql://10.62.73.58:5432/ab12?user=ab12&password=ab12&ssl=false";
        App.getInstance().setConnectionString(url);
        App.getInstance().Run();
    }
}
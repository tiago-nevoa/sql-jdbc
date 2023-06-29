/*
 * @Author: Matilde Pato (mpato)
 * @Date:   2023-05-26 18:55:00
 * @Last Modified time: 2023-05-26 18:55:00
 * ISEL - DEETC
 * Introdução a Sistemas de Informação
 * MPato, 2022-2023
 * 
 * NOTA:
 * Nesta classe deverá implementar todas as restrições de 
 * integridade descritas no enunciado, tais como:
 * 1. O atributo estado da tabela TRABALHO está correctamente preenchido. I.e., o atributo data de execução é NULL se o valor associado a estado é “planeado” ou “validado”.
 * 2. A data da inspecção expedita da obra de contenção é sempre superior à data planeada referida em TRABALHO.
 * 3. Um gestor não é inspector e vice-versa.

 */

package jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;


public class Restriction {
    private static  final String URL = "jdbc:postgresql://10.62.73.58:5432/";
    private static final String USER_NAME = "ab12";
    private static final String PASSWORD = "ab12";

    public static void estadoTrabalho() {
        Connection conn = null;
        ResultSet rs = null;
        Statement stmt = null;

        try {
            conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
            stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);

            // Delete rows from "campanha_monitorizacao" table where estado is "planeado" or "validado"
            String deleteSql = "DELETE FROM campanha_monitorizacao WHERE id_trabalho IN (" +
                    "SELECT id FROM TRABALHO WHERE estado = 'planeado' OR estado = 'validado')";
            stmt.executeUpdate(deleteSql);

            // Fetch the ResultSet after the delete operation
            String selectSql = "SELECT * FROM TRABALHO";
            rs = stmt.executeQuery(selectSql);

            while (rs.next()) {
                if (rs.getString("estado").equals("planeado") || rs.getString("estado").equals("validado")) {
                    // Update the "data_execucao" column to NULL
                    rs.updateNull("data_execucao");
                    rs.updateRow();

                }
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            // Close the ResultSet, Statement, and Connection in the reverse order of their creation
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }





 static public void inspetorGestor(){
    Connection conn= null;
    ResultSet rs= null;
    Statement stmt= null;
    try {
        conn= DriverManager.getConnection(URL, USER_NAME, PASSWORD);
        String sql = "SELECT * FROM TRABALHO";
         stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
         rs = stmt.executeQuery(sql);
        while (rs.next()){
            if(rs.getString("inspetor")==rs.getString("gestor")) {
                rs.deleteRow();
                System.out.println("O inspetor não pode ser igual ao gestor");
            }

    }
        rs.close();
        stmt.close();

} catch (SQLException e) {
        throw new RuntimeException(e);
    }}
    public static void enforceDateConstraint() {
        Connection conn = null;
        Statement stmt = null;

        try {
            conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
            stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            String sql = "SELECT * FROM TRABALHO";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                if (rs.getString("estado").equals("planeado") || rs.getString("estado").equals("validado")) {
                    Date dataPlanejada = rs.getDate("data_planeada");
                    Date dataExecucao = rs.getDate("data_execucao");

                    int idObra = rs.getInt("id_obra");
                    String inspecaoSql = "SELECT data FROM INSPECAO_EXPEDITA WHERE id_obra = " + idObra;
                    Statement inspecaoStmt = conn.createStatement();
                    ResultSet inspecaoRs = inspecaoStmt.executeQuery(inspecaoSql);
                    if (inspecaoRs.next()) {
                        Date inspecaoDate = inspecaoRs.getDate("data");

                        if (dataPlanejada.compareTo(inspecaoDate) <= 0 || (dataExecucao != null && dataExecucao.compareTo(inspecaoDate) <= 0)) {
                            int trabalhoId = rs.getInt("id");

                            String deleteChildSql1 = "DELETE FROM campanha_monitorizacao WHERE id_trabalho = " + trabalhoId;
                            stmt.executeUpdate(deleteChildSql1);

                            rs.refreshRow();
                            rs.updateNull("data_execucao");
                            rs.updateRow();
                            System.out.println("Constraint violation: The date of the TRABALHO is not greater than the date of the inspeção expedita.");
                        }
                    }

                    inspecaoRs.close();
                    inspecaoStmt.close();
                }
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}





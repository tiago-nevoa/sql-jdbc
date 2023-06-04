/*
 * @Author: Matilde Pato (mpato)
 * @Date:   2023-05-26 18:55:00
 * @Last Modified time: 2023-05-26 18:55:00
 * ISEL - DEETC
 * Introdução a Sistemas de Informação
 * MPato, 2022-2023
 * 
 * Classe para o objecto inspecção principal referente à tabela INSPECAO_PRINCIPAL
 * Já se encontra implementada.
 */ 

package jdbc;

class PrincipalInspection {
    private int workid;
    private int idx_condition;
    private int state;
    
    PrincipalInspection(String values){
        String[] attr = values.split(",");
        workid = Integer.parseInt(attr[0]);
        idx_condition = Integer.parseInt(attr[1]);
        state = Integer.parseInt(attr[2]);
    }

    public Integer getWorkID() { return workid; }

    public Integer getCondition(){ return  idx_condition; }

    public Integer getState() { return state; }

 
    public void setWorkID(int workid) { this.workid = workid; }

    public void setCondition(int idx_condition) { this.idx_condition = idx_condition; }

    public void setState(int state) { this.state = state; }

}

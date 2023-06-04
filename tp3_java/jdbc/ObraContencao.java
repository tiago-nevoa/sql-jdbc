package jdbc;
import java.time.LocalDate;

class Department {
    private int dnumber;
    private String dname;
    private String ename;
    private LocalDate date;
    
    Department(String values){
        String[] attr = values.split(",");
        dnumber = Integer.parseInt(attr[0]);
        dname = attr[1];
        ename = attr[2];
        LocalDate date = LocalDate.now(); // get automatically
    }

    public String getEmployee() { return ename; }

    public String getName(){ return  dname; }

    public Integer getNumber() { return dnumber; }

    public LocalDate getDate(){ return date; }
 
    public void setEmployee(String ename) { this.ename = ename; }

    public void setName(String dname) { this.dname = dname; }

    public void setNumber(int dnumber) { this.dnumber = dnumber; }

}

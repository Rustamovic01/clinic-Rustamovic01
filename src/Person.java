package clinic;

public class Person {
    private String firstName;
    private String lastName;
    private String ssn;
    private Doctor assignedDoctor;

    public Person(String firstName, String lastName, String ssn) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.ssn = ssn;
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getSSN() { return ssn; }
    public Doctor getDoctor() { return assignedDoctor; }
    public void setDoctor(Doctor doctor) { this.assignedDoctor = doctor; }
}

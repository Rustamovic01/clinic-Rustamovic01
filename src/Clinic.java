package clinic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Clinic {
    private Map<String, Person> patients = new HashMap<>();
    private Map<String, Doctor> doctors = new HashMap<>();

    public void addPatient(String firstName, String lastName, String ssn) {
        patients.put(ssn, new Person(firstName, lastName, ssn));
    }

    public Person getPatient(String ssn) throws NoSuchPatient {
        Person p = patients.get(ssn);
        if (p == null) throw new NoSuchPatient();
        return p;
    }

    public void addDoctor(String firstName, String lastName, String ssn, String badgeID, String specialization) {
        Doctor d = new Doctor(firstName, lastName, ssn, badgeID, specialization);
        doctors.put(badgeID, d);
        patients.put(ssn, d); // Add doctor as patient too
    }

    public Doctor getDoctor(String badgeID) throws NoSuchDoctor {
        Doctor d = doctors.get(badgeID);
        if (d == null) throw new NoSuchDoctor();
        return d;
    }

    public void assignPatientToDoctor(String ssn, String badgeID) throws NoSuchPatient, NoSuchDoctor {
        Person p = getPatient(ssn);
        Doctor d = getDoctor(badgeID);
        d.addPatient(p);
        p.setDoctor(d);
    }

    public void loadData(String path) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    String[] parts = line.split(";");
                    if (parts[0].equals("P") && parts.length == 4) {
                        addPatient(parts[1], parts[2], parts[3]);
                    } else if (parts[0].equals("M") && parts.length == 6) {
                        addDoctor(parts[2], parts[3], parts[4], parts[1], parts[5]);
                    }
                } catch (Exception ignored) {}
            }
        }
    }

    public Collection<Doctor> idleDoctors() {
        return doctors.values().stream()
                .filter(d -> d.getPatients().isEmpty())
                .sorted(Comparator.comparing(Doctor::getFirstName))
                .collect(Collectors.toList());
    }

    public Collection<Doctor> busyDoctors() {
        double avg = doctors.values().stream()
                .mapToInt(d -> d.getPatients().size()).average().orElse(0);
        return doctors.values().stream()
                .filter(d -> d.getPatients().size() > avg)
                .sorted(Comparator.comparing(Doctor::getFirstName))
                .collect(Collectors.toList());
    }

    public List<String> doctorsByNumPatients() {
        return doctors.values().stream()
                .sorted(Comparator.comparingInt((Doctor d) -> d.getPatients().size()).reversed())
                .map(d -> String.format("%3d: %s %s", d.getPatients().size(), d.getFirstName(), d.getLastName()))
                .collect(Collectors.toList());
    }

    public List<String> countPatientsPerSpecialization() {
        Map<String, Long> specMap = doctors.values().stream()
                .collect(Collectors.groupingBy(Doctor::getSpecialization,
                        Collectors.summingLong(d -> d.getPatients().size())));

        return specMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey()))
                .map(e -> String.format("%3d %s", e.getValue(), e.getKey()))
                .collect(Collectors.toList());
    }
}

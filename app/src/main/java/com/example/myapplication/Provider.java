package com.example.myapplication;
import java.util.ArrayList;

public class Provider extends User{
    private final ArrayList<Integer> patients;

    public Provider(int id, String name, String email, String password, String role) {
        super(id, name, role, email, password);
        patients = new ArrayList<>(); // Using diamond operator
    }

    /** This method adds an existing patient into the list of the provider's patients.
     * @param id of the patient
     */
    public void addPatient(int id) {
        patients.add(id); // Java autoboxing handles the conversion from int to Integer
    }

    // Public Getters and Setters
    public ArrayList<Integer> getPatients() {
        return patients;
    }

    // Setter for patients is omitted; the addPatient method controls modification.

    // ----- Sharing invitation -----
    public HealthProfile redeemInvite(Parent parent, String code) {
        SharedAccessInvite invite = parent.getInviteByCode(code);
        if (invite != null && invite.isValid()) {
            invite.markAsUsed();
            Child child = /* lookup by invite.getChildID() */;
            return filterProfileByInvite(child.getHealthProfile(), invite);
        }
        return null;
    }

    private HealthProfile filterProfileByInvite(HealthProfile full, SharedAccessInvite invite) {
        HealthProfile shared = new HealthProfile();
        for (HealthInfo field : invite.getSharedFields()) {
            switch (field) {
                case RESCUE_LOGS -> shared.addRescueLog(full.getRescueLogs());
                case CONTROLLER_ADHERENCE -> shared.addControllerAdherence(full.getControllerAdherence());
                case SYMPTOMS -> shared.addSymptom(full.getSymptoms());
                case TRIGGERS -> shared.addTrigger(full.getTriggers());
                case PEF -> shared.setPEF(full.getPEF());
                case TRIAGE_INCIDENTS -> shared.addTriageIncident(full.getTriageIncidents());
                case CHARTS -> shared.addChart(full.getCharts());
            }
        }
        return shared;
    }

}
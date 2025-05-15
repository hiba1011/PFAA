package com.emsi.gestionuniv.repository;

import com.emsi.gestionuniv.model.user.Teacher;
import java.util.ArrayList;
import java.util.List;

//Constructeur
public class TeacherRepository {
    // Liste qui stocke les enseignants (simulant une base de données)
    private List<Teacher> teachers;
    public TeacherRepository() {
        // Initialisation de la liste des enseignants
        teachers = new ArrayList<>();

        // Ajout d'enseignants avec leurs informations (id, nom, prénom, email, mot de passe, spécialité, département)
        teachers.add(new Teacher(1, "Alaoui", "Ahmed", "ahmed.alaoui@emsi.ma", "pass123", "Mathématiques", "Sciences"));
        teachers.add(new Teacher(2, "Benani", "Fatima", "fatima.benani@emsi.ma", "pass123", "Informatique", "Technologie"));
        teachers.add(new Teacher(3, "Chaoui", "Mohammed", "mohammed.chaoui@emsi.ma", "pass123", "Physique", "Sciences"));
    }

    public Teacher findByEmail(String email) {
        // Parcours de la liste des enseignants
        for (Teacher t : teachers) {
            // Comparaison des emails (sans tenir compte de la casse)
            if (t.getEmail().equalsIgnoreCase(email)) {
                return t;
            }
        }
        // Aucun enseignant trouvé avec cet email
        return null;
    }
}
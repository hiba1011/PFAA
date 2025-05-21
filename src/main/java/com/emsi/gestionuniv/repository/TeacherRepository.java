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
package com.emsi.gestionuniv.repository;

import com.emsi.gestionuniv.model.user.Student;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository pour gérer les étudiants
 */
public class StudentRepository {
    // Liste qui stocke les étudiants (simulant une base de données)
    private List<Student> students;

    /**
     * Constructeur initialisant la liste des étudiants
     */
    public StudentRepository() {
        // Initialisation de la liste des étudiants
        students = new ArrayList<>();
    }

    /**
     * Ajoute un étudiant à la liste
     * @param student L'étudiant à ajouter
     */
    public void add(Student student) {
        students.add(student);
    }

    /**
     * Supprime un étudiant de la liste
     * @param student L'étudiant à supprimer
     * @return true si l'étudiant a été supprimé, false sinon
     */
    public boolean remove(Student student) {
        return students.remove(student);
    }

    /**
     * Recherche un étudiant par son matricule
     * @param matricule Matricule de l'étudiant à rechercher
     * @return L'étudiant trouvé ou null si aucun ne correspond
     */
    public Student findByMatricule(String matricule) {
        for (Student s : students) {
            if (s.getMatricule().equals(matricule)) {
                return s;
            }
        }
        return null;
    }

    /**
     * Récupère tous les étudiants
     * @return Liste de tous les étudiants
     */
    public List<Student> findAll() {
        return new ArrayList<>(students);
    }

    /**
     * Recherche des étudiants par promotion
     * @param promotion La promotion à rechercher
     * @return Liste des étudiants de cette promotion
     */
    public List<Student> findByPromotion(String promotion) {
        List<Student> result = new ArrayList<>();
        for (Student s : students) {
            if (s.getPromotion().equals(promotion)) {
                result.add(s);
            }
        }
        return result;
    }

    /**
     * Recherche des étudiants par filière
     * @param filiere La filière à rechercher
     * @return Liste des étudiants de cette filière
     */
    public List<Student> findByFiliere(String filiere) {
        List<Student> result = new ArrayList<>();
        for (Student s : students) {
            if (s.getFiliere().equals(filiere)) {
                result.add(s);
            }
        }
        return result;
    }

    /**
     * Recherche un étudiant par son email
     * @param email Email de l'étudiant à rechercher
     * @return L'étudiant trouvé ou null si aucun ne correspond
     */
    public Student findByEmail(String email) {
        // Parcours de la liste des étudiants
        for (Student s : students) {
            // Comparaison des emails (sans tenir compte de la casse)
            if (s.getEmail().equalsIgnoreCase(email)) {
                return s;
            }
        }
        // Aucun étudiant trouvé avec cet email
        return null;
    }
}
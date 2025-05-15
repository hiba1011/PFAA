package com.emsi.gestionuniv.service;

import com.emsi.gestionuniv.model.user.Teacher;
import com.emsi.gestionuniv.repository.TeacherRepository;

public class TeacherService {
    private TeacherRepository teacherRepository;

    public TeacherService() {
        this.teacherRepository = new TeacherRepository();
    }

    /**
     * Authentifie un enseignant avec son email et mot de passe.
     */
    public boolean authenticate(String email, String password) {
        Teacher teacher = teacherRepository.findByEmail(email);
        return teacher != null && teacher.getPassword().equals(password);
    }

    /**
     * Retourne l'objet Teacher correspondant à l'email,
     * ou null si aucun enseignant n'est trouvé.
     */
    public Teacher findByEmail(String email) {
        return teacherRepository.findByEmail(email);
    }

    // ... autres méthodes métier si besoin
}

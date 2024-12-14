package com.freelancer.app.services;

import com.freelancer.app.models.User;
import com.freelancer.app.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;
    @PersistenceContext
    private EntityManager entityManager;

    public User get(Long id){
        return userRepository.findOne(id);
    }
    public User getByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public User save(User user){
		user.setPassword( passwordEncoder.encode( user.getPassword() )  );
        return userRepository.save(user);
    }

    public List<String> findUsersBySkills(List<String> requiredSkills) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder(); //build the query.
        CriteriaQuery<String> query = cb.createQuery(String.class); //specify the structure of the query.
        Root<User> user = query.from(User.class);

        System.out.println(user.toString()+">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

        // Select email column
        query.select(user.get("email")).distinct(true);

        // Build predicates for LIKE conditions
        List<Predicate> predicates = new ArrayList<>();
        for (String skill : requiredSkills) {
            predicates.add(cb.like(user.get("profile").get("skills"), "%" + skill + "%"));
        }

        // Combine predicates with OR
        query.where(cb.or(predicates.toArray(new Predicate[0])));

        // Execute query
        return entityManager.createQuery(query).getResultList();
    }

}

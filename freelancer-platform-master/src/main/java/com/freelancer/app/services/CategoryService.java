package com.freelancer.app.services;

import com.freelancer.app.models.Category;
import com.freelancer.app.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

        @Autowired
        CategoryRepository categoryRepository;

        public Category get(long id){
            return categoryRepository.findOne(id);
        }

        public Category add(Category category){
            return categoryRepository.save(category);
        }

        public List<Category> list(){
            return categoryRepository.findAll();
        }


}

package com.example.codefellowship.repository;

import com.example.codefellowship.model.UserDataModel;
import org.springframework.data.repository.CrudRepository;

public interface DbUserDataRepository extends CrudRepository<UserDataModel,Integer> {
    public UserDataModel findByUsername(String username);
}
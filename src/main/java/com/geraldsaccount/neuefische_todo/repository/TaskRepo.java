package com.geraldsaccount.neuefische_todo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.geraldsaccount.neuefische_todo.model.Task;

@Repository
public interface TaskRepo extends MongoRepository<Task, String> {

}

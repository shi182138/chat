package com.example.chatt.dao;

import com.example.chatt.pojo.AccountPool;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AccountPoolDao extends MongoRepository<AccountPool, String> {

}

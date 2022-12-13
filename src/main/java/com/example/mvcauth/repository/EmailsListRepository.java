package com.example.mvcauth.repository;

import com.example.mvcauth.entities.EmailsList;
import com.example.mvcauth.entities.key.EmailsListPrimaryKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailsListRepository extends CassandraRepository<EmailsList, EmailsListPrimaryKey>  {
    List<EmailsList> findAllById_UserEmailAndId_Label(String userId, String label);
}
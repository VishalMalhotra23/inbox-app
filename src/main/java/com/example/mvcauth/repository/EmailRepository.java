package com.example.mvcauth.repository;

import java.util.UUID;

import com.example.mvcauth.entities.Email;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailRepository extends CassandraRepository<Email, UUID>  {
}

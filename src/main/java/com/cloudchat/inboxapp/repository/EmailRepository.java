package com.cloudchat.inboxapp.repository;

import java.util.UUID;

import com.cloudchat.inboxapp.models.Email;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailRepository extends CassandraRepository<Email, UUID>  {
}

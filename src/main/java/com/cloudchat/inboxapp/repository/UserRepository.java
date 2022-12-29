package com.cloudchat.inboxapp.repository;

import com.cloudchat.inboxapp.models.User;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CassandraRepository<User, String> {
}

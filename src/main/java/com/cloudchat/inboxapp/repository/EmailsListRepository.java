package com.cloudchat.inboxapp.repository;

import com.cloudchat.inboxapp.models.key.EmailsListPrimaryKey;
import com.cloudchat.inboxapp.models.EmailsList;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailsListRepository extends CassandraRepository<EmailsList, EmailsListPrimaryKey>  {
    List<EmailsList> findAllById_UserEmailAndId_Label(String userId, String label);
}
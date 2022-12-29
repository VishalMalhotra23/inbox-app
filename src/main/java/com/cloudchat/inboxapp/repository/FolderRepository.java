package com.cloudchat.inboxapp.repository;

import java.util.List;

import com.cloudchat.inboxapp.models.Folder;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FolderRepository extends CassandraRepository<Folder, String> {
    List<Folder> findAllById(String id);
}

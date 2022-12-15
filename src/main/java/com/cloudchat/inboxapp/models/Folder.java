package com.cloudchat.inboxapp.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table(value = "folders_by_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Folder {

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String id;

    @PrimaryKeyColumn(name = "label", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING)
    @CassandraType(type = CassandraType.Name.TEXT)
    private String label;

    @CassandraType(type = CassandraType.Name.TEXT)
    private String color;

    @Transient
    private int unreadCount;

    public Folder(String id, String label, String color) {
        this.id = id;
        this.label = label;
        this.color = color;
    }
}
package com.example.mvcauth.entities;

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

    // @PrimaryKeyColumn(name = "created_time_uuid", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING)
    // @CassandraType(type = CassandraType.Name.TEXT)
    // private UUID createdTimeUuid;

    @CassandraType(type = CassandraType.Name.TEXT)
    private String color;

    @Transient
    private int unreadCount;

}
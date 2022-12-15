package com.cloudchat.inboxapp.models;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.CassandraType.Name;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table(value = "emails_by_id")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Email {

    @Id @PrimaryKeyColumn(name = "id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    @CassandraType(type = CassandraType.Name.TIMEUUID)
    private UUID id;

    @CassandraType(type = CassandraType.Name.TEXT)
    private String from;

    @CassandraType(type = CassandraType.Name.LIST, typeArguments = Name.TEXT)
    private List<String> to;

    @CassandraType(type = CassandraType.Name.TEXT)
    private String subject;

    @CassandraType(type = CassandraType.Name.TEXT)
    private String body;
}

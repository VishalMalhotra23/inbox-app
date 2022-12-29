package com.cloudchat.inboxapp.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(value = "oauth_user")
public class User {

    @PrimaryKeyColumn(
            name = "user_id",
            ordinal = 0,
            type = PrimaryKeyType.CLUSTERED
    )
    @CassandraType(
            type = CassandraType.Name.TEXT
    )
    private String userId;

    @PrimaryKeyColumn(
            name = "user_email",
            ordinal = 1,
            type = PrimaryKeyType.PARTITIONED
    )
    private String email;

    private String name;

}

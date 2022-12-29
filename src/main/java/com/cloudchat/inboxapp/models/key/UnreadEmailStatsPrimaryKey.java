package com.cloudchat.inboxapp.models.key;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UnreadEmailStatsPrimaryKey {

    @PrimaryKeyColumn(name = "user_email", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String userEmail;

    @PrimaryKeyColumn(name = "label", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    private String label;

}

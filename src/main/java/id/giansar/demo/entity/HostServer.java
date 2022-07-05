package id.giansar.demo.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "host_server", indexes = @Index(columnList = "host, server"))
public class HostServer extends PanacheEntityBase {
    @Id
    @GeneratedValue
    public UUID id;
    @Column(name = "host", unique = true, nullable = false)
    public String host;
    @Column(name = "server")
    public String server;
    @Column(name = "date_inquiry", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    public ZonedDateTime dateInquiry;
}

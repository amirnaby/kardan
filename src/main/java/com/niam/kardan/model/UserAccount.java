package com.niam.kardan.model;

import com.niam.usermanagement.model.entities.Auditable;
import com.niam.usermanagement.model.entities.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "user_account")
@SequenceGenerator(name = "user_account_seq", sequenceName = "user_account_seq", allocationSize = 1)
public class UserAccount extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_account_seq")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true, nullable = false)
    private User user;

    @Column(nullable = false)
    private Set<String> types;

    @Column(nullable = false, unique = true)
    private String personnelCode;
}
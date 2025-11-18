package com.niam.kardan.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.niam.kardan.model.basedata.PartStatus;
import com.niam.usermanagement.model.entities.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Entity(name = "Part")
@Table(name = "part")
@SequenceGenerator(name = "part_seq", sequenceName = "part_seq", allocationSize = 1)
public class Part extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "part_seq")
    private Long id;
    @NotNull
    @NotBlank
    private String name;
    private String description;
    @Lob
    private String manufacturingPlan;
    @NotNull
    @NotBlank
    @Column(unique = true)
    private String code;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", referencedColumnName = "id", nullable = false)
    private Project project;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", referencedColumnName = "id", nullable = false)
    private PartStatus status;
}
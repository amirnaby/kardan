package com.niam.kardan.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.niam.kardan.model.basedata.StopReasonCategory;
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
@Entity(name = "StopReason")
@Table(name = "stop_reason")
@SequenceGenerator(name = "stop_reason_seq", sequenceName = "stop_reason_seq", allocationSize = 1)
public class StopReason extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stop_reason_seq")
    private Long id;
    @NotNull
    @NotBlank
    @Column(unique = true)
    private String name;
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false)
    private StopReasonCategory category;
}
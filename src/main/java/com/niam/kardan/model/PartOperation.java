package com.niam.kardan.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.niam.kardan.model.basedata.PartOperationStatus;
import com.niam.usermanagement.model.entities.Auditable;
import jakarta.persistence.*;
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
@Entity(name = "PartOperation")
@Table(name = "part_operation", uniqueConstraints = @UniqueConstraint(columnNames = {"part_id", "sequence"}))
@SequenceGenerator(name = "part_operation_seq", sequenceName = "part_operation_seq", allocationSize = 1)
public class PartOperation extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "part_operation_seq")
    private Long id;
    @NotNull
    private Integer sequence;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id", referencedColumnName = "id", nullable = false)
    private Part part;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operation_id", referencedColumnName = "id", nullable = false)
    private Operation operation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machine_id", referencedColumnName = "id", nullable = false)
    private Machine machine;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", referencedColumnName = "id", nullable = false)
    private PartOperationStatus partOperationStatus;
    private Long estimatedDuration;
}
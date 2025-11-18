package com.niam.kardan.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.niam.kardan.model.basedata.MachineStatus;
import com.niam.kardan.model.basedata.MachineType;
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
@Entity(name = "Machine")
@Table(name = "machine")
@SequenceGenerator(name = "machine_seq", sequenceName = "machine_seq", allocationSize = 1)
public class Machine extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "machine_seq")
    private Long id;
    @NotNull
    @NotBlank
    @Column(unique = true)
    private String code;
    private String location;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machine_type_id", referencedColumnName = "id", nullable = false)
    private MachineType machineType;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", referencedColumnName = "id", nullable = false)
    private MachineStatus machineStatus;
}
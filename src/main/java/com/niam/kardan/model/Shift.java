package com.niam.kardan.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niam.common.utils.CustomLocalDateTimeDeserializer;
import com.niam.common.utils.CustomLocalDateTimeSerializer;
import com.niam.kardan.model.basedata.ShiftStatus;
import com.niam.usermanagement.model.entities.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Entity(name = "Shift")
@Table(name = "shift")
@SequenceGenerator(name = "shift_seq", sequenceName = "shift_seq", allocationSize = 1)
public class Shift extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shift_seq")
    private Long id;
    @NotNull
    @NotBlank
    @Column(unique = true)
    private String name;
    private String description;
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    private LocalDateTime startTime;
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    private LocalDateTime endTime;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", referencedColumnName = "id", nullable = false)
    private ShiftStatus status;
}
package com.niam.kardan.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niam.common.utils.CustomLocalDateTimeDeserializer;
import com.niam.common.utils.CustomLocalDateTimeSerializer;
import com.niam.usermanagement.model.entities.Auditable;
import jakarta.persistence.*;
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
@Entity(name = "OperationStop")
@Table(name = "operation_stop")
@SequenceGenerator(name = "operation_stop_seq", sequenceName = "operation_stop_seq", allocationSize = 1)
public class OperationStop extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "operation_stop_seq")
    private Long id;
    private String comment;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operation_execution_id", referencedColumnName = "id", nullable = false)
    private OperationExecution operationExecution;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stop_reason_id", referencedColumnName = "id", nullable = false)
    private StopReason stopReason;
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    private LocalDateTime startedAt;
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    private LocalDateTime endedAt;
}
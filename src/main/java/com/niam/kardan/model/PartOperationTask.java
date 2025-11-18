package com.niam.kardan.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niam.common.utils.CustomLocalDateTimeDeserializer;
import com.niam.common.utils.CustomLocalDateTimeSerializer;
import com.niam.kardan.model.basedata.TaskStatus;
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
@Entity(name = "PartOperationTask")
@Table(name = "part_operation_task")
@SequenceGenerator(name = "part_operation_task_seq", sequenceName = "part_operation_task_seq", allocationSize = 1)
public class PartOperationTask extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "part_operation_task_seq")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_operation_id", referencedColumnName = "id", nullable = false)
    private PartOperation partOperation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_machine_id", referencedColumnName = "id", nullable = false)
    private Machine targetMachine;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_task_id")
    private PartOperationTask parentTask;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claimed_by_operator_id")
    private Operator claimedBy;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", referencedColumnName = "id", nullable = false)
    private TaskStatus taskStatus;
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    private LocalDateTime claimedAt;
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    private LocalDateTime startedAt;
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    private LocalDateTime finishedAt;
}
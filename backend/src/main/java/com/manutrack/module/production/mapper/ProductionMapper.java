package com.manutrack.module.production.mapper;

import com.manutrack.module.production.dto.ProductionDtos;
import com.manutrack.module.production.entity.Machine;
import com.manutrack.module.production.entity.ProductionPlan;
import com.manutrack.module.production.entity.WorkOrder;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductionMapper {

    ProductionDtos.PlanResponse toPlanResponse(ProductionPlan plan);

    @Mapping(target = "planId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProductionPlan toPlan(ProductionDtos.CreatePlanRequest req);

    void updatePlan(ProductionDtos.UpdatePlanRequest req, @MappingTarget ProductionPlan plan);

    ProductionDtos.MachineResponse toMachineResponse(Machine machine);

    @Mapping(target = "machineId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Machine toMachine(ProductionDtos.CreateMachineRequest req);

    void updateMachine(ProductionDtos.UpdateMachineRequest req, @MappingTarget Machine machine);

    @Mapping(target = "planId", source = "plan.planId")
    @Mapping(target = "planName", source = "plan.planName")
    @Mapping(target = "machineId", source = "machine.machineId")
    @Mapping(target = "machineName", source = "machine.name")
    ProductionDtos.WorkOrderResponse toWorkOrderResponse(WorkOrder wo);

    void updateWorkOrder(ProductionDtos.UpdateWorkOrderRequest req, @MappingTarget WorkOrder wo);
}

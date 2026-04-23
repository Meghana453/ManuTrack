package com.manutrack.module.production.mapper;

import com.manutrack.module.production.dto.ProductionDtos;
import com.manutrack.module.production.entity.Machine;
import com.manutrack.module.production.entity.ProductionPlan;
import com.manutrack.module.production.entity.WorkOrder;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-26T18:22:21+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.2 (Oracle Corporation)"
)
@Component
public class ProductionMapperImpl implements ProductionMapper {

    @Override
    public ProductionDtos.PlanResponse toPlanResponse(ProductionPlan plan) {
        if ( plan == null ) {
            return null;
        }

        ProductionDtos.PlanResponse planResponse = new ProductionDtos.PlanResponse();

        planResponse.setPlanId( plan.getPlanId() );
        planResponse.setPlantId( plan.getPlantId() );
        planResponse.setPlanName( plan.getPlanName() );
        planResponse.setStartDate( plan.getStartDate() );
        planResponse.setEndDate( plan.getEndDate() );
        planResponse.setTargetUnits( plan.getTargetUnits() );
        planResponse.setStatus( plan.getStatus() );
        planResponse.setCreatedAt( plan.getCreatedAt() );

        return planResponse;
    }

    @Override
    public ProductionPlan toPlan(ProductionDtos.CreatePlanRequest req) {
        if ( req == null ) {
            return null;
        }

        ProductionPlan.ProductionPlanBuilder productionPlan = ProductionPlan.builder();

        productionPlan.plantId( req.getPlantId() );
        productionPlan.planName( req.getPlanName() );
        productionPlan.startDate( req.getStartDate() );
        productionPlan.endDate( req.getEndDate() );
        productionPlan.targetUnits( req.getTargetUnits() );
        productionPlan.status( req.getStatus() );

        return productionPlan.build();
    }

    @Override
    public void updatePlan(ProductionDtos.UpdatePlanRequest req, ProductionPlan plan) {
        if ( req == null ) {
            return;
        }

        if ( req.getPlanName() != null ) {
            plan.setPlanName( req.getPlanName() );
        }
        if ( req.getStartDate() != null ) {
            plan.setStartDate( req.getStartDate() );
        }
        if ( req.getEndDate() != null ) {
            plan.setEndDate( req.getEndDate() );
        }
        if ( req.getTargetUnits() != null ) {
            plan.setTargetUnits( req.getTargetUnits() );
        }
        if ( req.getStatus() != null ) {
            plan.setStatus( req.getStatus() );
        }
    }

    @Override
    public ProductionDtos.MachineResponse toMachineResponse(Machine machine) {
        if ( machine == null ) {
            return null;
        }

        ProductionDtos.MachineResponse machineResponse = new ProductionDtos.MachineResponse();

        machineResponse.setMachineId( machine.getMachineId() );
        machineResponse.setPlantId( machine.getPlantId() );
        machineResponse.setName( machine.getName() );
        machineResponse.setCapacity( machine.getCapacity() );
        machineResponse.setStatus( machine.getStatus() );
        machineResponse.setCreatedAt( machine.getCreatedAt() );

        return machineResponse;
    }

    @Override
    public Machine toMachine(ProductionDtos.CreateMachineRequest req) {
        if ( req == null ) {
            return null;
        }

        Machine.MachineBuilder machine = Machine.builder();

        machine.plantId( req.getPlantId() );
        machine.name( req.getName() );
        machine.capacity( req.getCapacity() );
        machine.status( req.getStatus() );

        return machine.build();
    }

    @Override
    public void updateMachine(ProductionDtos.UpdateMachineRequest req, Machine machine) {
        if ( req == null ) {
            return;
        }

        if ( req.getName() != null ) {
            machine.setName( req.getName() );
        }
        if ( req.getCapacity() != null ) {
            machine.setCapacity( req.getCapacity() );
        }
        if ( req.getStatus() != null ) {
            machine.setStatus( req.getStatus() );
        }
    }

    @Override
    public ProductionDtos.WorkOrderResponse toWorkOrderResponse(WorkOrder wo) {
        if ( wo == null ) {
            return null;
        }

        ProductionDtos.WorkOrderResponse workOrderResponse = new ProductionDtos.WorkOrderResponse();

        workOrderResponse.setPlanId( woPlanPlanId( wo ) );
        workOrderResponse.setPlanName( woPlanPlanName( wo ) );
        workOrderResponse.setMachineId( woMachineMachineId( wo ) );
        workOrderResponse.setMachineName( woMachineName( wo ) );
        workOrderResponse.setWorkOrderId( wo.getWorkOrderId() );
        workOrderResponse.setProductId( wo.getProductId() );
        workOrderResponse.setQuantity( wo.getQuantity() );
        workOrderResponse.setScheduledStart( wo.getScheduledStart() );
        workOrderResponse.setScheduledEnd( wo.getScheduledEnd() );
        workOrderResponse.setActualStart( wo.getActualStart() );
        workOrderResponse.setActualEnd( wo.getActualEnd() );
        workOrderResponse.setStatus( wo.getStatus() );
        workOrderResponse.setCreatedAt( wo.getCreatedAt() );

        return workOrderResponse;
    }

    @Override
    public void updateWorkOrder(ProductionDtos.UpdateWorkOrderRequest req, WorkOrder wo) {
        if ( req == null ) {
            return;
        }

        if ( req.getQuantity() != null ) {
            wo.setQuantity( req.getQuantity() );
        }
        if ( req.getScheduledStart() != null ) {
            wo.setScheduledStart( req.getScheduledStart() );
        }
        if ( req.getScheduledEnd() != null ) {
            wo.setScheduledEnd( req.getScheduledEnd() );
        }
        if ( req.getActualStart() != null ) {
            wo.setActualStart( req.getActualStart() );
        }
        if ( req.getActualEnd() != null ) {
            wo.setActualEnd( req.getActualEnd() );
        }
        if ( req.getStatus() != null ) {
            wo.setStatus( req.getStatus() );
        }
    }

    private Long woPlanPlanId(WorkOrder workOrder) {
        if ( workOrder == null ) {
            return null;
        }
        ProductionPlan plan = workOrder.getPlan();
        if ( plan == null ) {
            return null;
        }
        Long planId = plan.getPlanId();
        if ( planId == null ) {
            return null;
        }
        return planId;
    }

    private String woPlanPlanName(WorkOrder workOrder) {
        if ( workOrder == null ) {
            return null;
        }
        ProductionPlan plan = workOrder.getPlan();
        if ( plan == null ) {
            return null;
        }
        String planName = plan.getPlanName();
        if ( planName == null ) {
            return null;
        }
        return planName;
    }

    private Long woMachineMachineId(WorkOrder workOrder) {
        if ( workOrder == null ) {
            return null;
        }
        Machine machine = workOrder.getMachine();
        if ( machine == null ) {
            return null;
        }
        Long machineId = machine.getMachineId();
        if ( machineId == null ) {
            return null;
        }
        return machineId;
    }

    private String woMachineName(WorkOrder workOrder) {
        if ( workOrder == null ) {
            return null;
        }
        Machine machine = workOrder.getMachine();
        if ( machine == null ) {
            return null;
        }
        String name = machine.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}

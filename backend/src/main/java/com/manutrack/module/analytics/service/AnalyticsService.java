package com.manutrack.module.analytics.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manutrack.module.analytics.dto.AnalyticsDtos;
import com.manutrack.module.analytics.entity.OperationalReport;
import com.manutrack.module.analytics.repository.OperationalReportRepository;
import com.manutrack.module.inventory.entity.InventoryItem;
import com.manutrack.module.inventory.entity.MaterialRequest;
import com.manutrack.module.inventory.repository.InventoryItemRepository;
import com.manutrack.module.inventory.repository.MaterialRequestRepository;
import com.manutrack.module.logistics.entity.Carrier;
import com.manutrack.module.logistics.entity.Shipment;
import com.manutrack.module.logistics.repository.CarrierRepository;
import com.manutrack.module.logistics.repository.RouteRepository;
import com.manutrack.module.logistics.repository.ShipmentRepository;
import com.manutrack.module.procurement.entity.Invoice;
import com.manutrack.module.procurement.entity.PurchaseOrder;
import com.manutrack.module.procurement.entity.Vendor;
import com.manutrack.module.procurement.repository.InvoiceRepository;
import com.manutrack.module.procurement.repository.PurchaseOrderRepository;
import com.manutrack.module.procurement.repository.VendorRepository;
import com.manutrack.module.production.entity.Machine;
import com.manutrack.module.production.entity.ProductionPlan;
import com.manutrack.module.production.entity.WorkOrder;
import com.manutrack.module.production.repository.MachineRepository;
import com.manutrack.module.production.repository.ProductionPlanRepository;
import com.manutrack.module.production.repository.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service @RequiredArgsConstructor @Slf4j @Transactional(readOnly = true)
public class AnalyticsService {

    private final ProductionPlanRepository planRepo;
    private final WorkOrderRepository workOrderRepo;
    private final MachineRepository machineRepo;
    private final InventoryItemRepository itemRepo;
    private final MaterialRequestRepository materialRequestRepo;
    private final VendorRepository vendorRepo;
    private final PurchaseOrderRepository poRepo;
    private final InvoiceRepository invoiceRepo;
    private final ShipmentRepository shipmentRepo;
    private final CarrierRepository carrierRepo;
    private final RouteRepository routeRepo;
    private final OperationalReportRepository reportRepo;
    private final ObjectMapper objectMapper;

    public AnalyticsDtos.DashboardSummary getDashboardSummary() {
        return AnalyticsDtos.DashboardSummary.builder()
                .production(getProductionSummary())
                .inventory(getInventorySummary())
                .procurement(getProcurementSummary())
                .logistics(getLogisticsSummary())
                .generatedAt(LocalDateTime.now())
                .build();
    }

    public AnalyticsDtos.ProductionSummary getProductionSummary() {
        List<ProductionPlan> plans = planRepo.findAll();
        List<WorkOrder> workOrders = workOrderRepo.findAll();
        List<Machine> machines = machineRepo.findAll();
        return AnalyticsDtos.ProductionSummary.builder()
                .totalPlans(plans.size())
                .inProgressPlans(plans.stream().filter(p -> p.getStatus() == ProductionPlan.Status.IN_PROGRESS).count())
                .completedPlans(plans.stream().filter(p -> p.getStatus() == ProductionPlan.Status.COMPLETED).count())
                .totalWorkOrders(workOrders.size())
                .pendingWorkOrders(workOrders.stream().filter(w -> w.getStatus() == WorkOrder.Status.PENDING).count())
                .haltedWorkOrders(workOrders.stream().filter(w -> w.getStatus() == WorkOrder.Status.HALTED).count())
                .totalMachines(machines.size())
                .activeMachines(machines.stream().filter(m -> m.getStatus() == Machine.Status.ACTIVE).count())
                .maintenanceMachines(machines.stream().filter(m -> m.getStatus() == Machine.Status.MAINTENANCE).count())
                .build();
    }

    public AnalyticsDtos.InventorySummary getInventorySummary() {
        List<InventoryItem> items = itemRepo.findAll();
        List<MaterialRequest> requests = materialRequestRepo.findAll();
        return AnalyticsDtos.InventorySummary.builder()
                .totalItems(items.size())
                .availableItems(items.stream().filter(i -> i.getStatus() == InventoryItem.Status.AVAILABLE).count())
                .lowStockItems(items.stream().filter(i -> i.getStatus() == InventoryItem.Status.LOW_STOCK).count())
                .outOfStockItems(items.stream().filter(i -> i.getStatus() == InventoryItem.Status.OUT_OF_STOCK).count())
                .totalMaterialRequests(requests.size())
                .pendingMaterialRequests(requests.stream().filter(r -> r.getStatus() == MaterialRequest.Status.PENDING).count())
                .build();
    }

    public AnalyticsDtos.ProcurementSummary getProcurementSummary() {
        List<Vendor> vendors = vendorRepo.findAll();
        List<PurchaseOrder> pos = poRepo.findAll();
        List<Invoice> invoices = invoiceRepo.findAll();
        double totalPOValue = pos.stream().mapToDouble(p -> p.getQuantity() * p.getUnitPrice()).sum();
        return AnalyticsDtos.ProcurementSummary.builder()
                .totalVendors(vendors.size())
                .activeVendors(vendors.stream().filter(v -> v.getStatus() == Vendor.Status.ACTIVE).count())
                .totalPOs(pos.size())
                .openPOs(pos.stream().filter(p -> p.getStatus() == PurchaseOrder.Status.OPEN).count())
                .totalInvoices(invoices.size())
                .pendingInvoices(invoices.stream().filter(i -> i.getStatus() == Invoice.Status.PENDING).count())
                .overdueInvoices(invoices.stream().filter(i -> i.getStatus() == Invoice.Status.OVERDUE).count())
                .totalPOValue(totalPOValue)
                .build();
    }

    public AnalyticsDtos.LogisticsSummary getLogisticsSummary() {
        List<Shipment> shipments = shipmentRepo.findAll();
        return AnalyticsDtos.LogisticsSummary.builder()
                .totalShipments(shipments.size())
                .scheduledShipments(shipments.stream().filter(s -> s.getStatus() == Shipment.Status.SCHEDULED).count())
                .inTransitShipments(shipments.stream().filter(s -> s.getStatus() == Shipment.Status.IN_TRANSIT).count())
                .deliveredShipments(shipments.stream().filter(s -> s.getStatus() == Shipment.Status.DELIVERED).count())
                .delayedShipments(shipments.stream().filter(s -> s.getStatus() == Shipment.Status.DELAYED).count())
                .totalCarriers(carrierRepo.count())
                .totalRoutes(routeRepo.count())
                .build();
    }

    @Transactional
    public AnalyticsDtos.ReportResponse generateReport(String scope) {
        AnalyticsDtos.DashboardSummary summary = getDashboardSummary();
        String metricsJson;
        try { metricsJson = objectMapper.writeValueAsString(summary); }
        catch (Exception e) { metricsJson = "{}"; }
        OperationalReport report = OperationalReport.builder().scope(scope).metrics(metricsJson).build();
        report = reportRepo.save(report);
        return AnalyticsDtos.ReportResponse.builder()
                .reportId(report.getReportId()).scope(report.getScope())
                .metrics(report.getMetrics()).generatedDate(report.getGeneratedDate()).build();
    }

    public List<AnalyticsDtos.ReportResponse> getAllReports() {
        return reportRepo.findAll().stream().map(r -> AnalyticsDtos.ReportResponse.builder()
                .reportId(r.getReportId()).scope(r.getScope())
                .metrics(r.getMetrics()).generatedDate(r.getGeneratedDate()).build()).toList();
    }
}

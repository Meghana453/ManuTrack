//package com.manutrack.config;
//
//  Demo@gmail.com   Demo@12345
//
//import com.manutrack.module.inventory.entity.InventoryItem;
//import com.manutrack.module.inventory.repository.InventoryItemRepository;
//import com.manutrack.module.logistics.entity.Carrier;
//import com.manutrack.module.logistics.entity.Route;
//import com.manutrack.module.logistics.entity.Shipment;
//import com.manutrack.module.logistics.repository.CarrierRepository;
//import com.manutrack.module.logistics.repository.RouteRepository;
//import com.manutrack.module.logistics.repository.ShipmentRepository;
//import com.manutrack.module.notification.entity.Notification;
//import com.manutrack.module.notification.repository.NotificationRepository;
//import com.manutrack.module.procurement.entity.Invoice;
//import com.manutrack.module.procurement.entity.PurchaseOrder;
//import com.manutrack.module.procurement.entity.Vendor;
//import com.manutrack.module.procurement.repository.InvoiceRepository;
//import com.manutrack.module.procurement.repository.PurchaseOrderRepository;
//import com.manutrack.module.procurement.repository.VendorRepository;
//import com.manutrack.module.production.entity.Machine;
//import com.manutrack.module.production.entity.ProductionPlan;
//import com.manutrack.module.production.entity.WorkOrder;
//import com.manutrack.module.production.repository.MachineRepository;
//import com.manutrack.module.production.repository.ProductionPlanRepository;
//import com.manutrack.module.production.repository.WorkOrderRepository;
//import com.manutrack.module.iam.entity.User;
//import com.manutrack.module.iam.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//
//@Component @RequiredArgsConstructor @Slf4j
//public class DataSeeder implements CommandLineRunner {
//
//    private final UserRepository userRepo;
//    private final ProductionPlanRepository planRepo;
//    private final MachineRepository machineRepo;
//    private final WorkOrderRepository workOrderRepo;
//    private final InventoryItemRepository itemRepo;
//    private final VendorRepository vendorRepo;
//    private final PurchaseOrderRepository poRepo;
//    private final InvoiceRepository invoiceRepo;
//    private final CarrierRepository carrierRepo;
//    private final RouteRepository routeRepo;
//    private final ShipmentRepository shipmentRepo;
//    private final NotificationRepository notifRepo;
//    private final PasswordEncoder passwordEncoder;
//
//    @Override
//    public void run(String... args) {
//        if (userRepo.count() > 0) { log.info("Data already seeded, skipping."); return; }
//        log.info("Seeding demo data...");
//
//        // Users
//        User admin = userRepo.save(User.builder().name("Admin User").role(User.Role.ADMIN)
//                .email("admin@manutrack.com").password(passwordEncoder.encode("admin123")).phone("555-0001").build());
//        User planner = userRepo.save(User.builder().name("Alice Planner").role(User.Role.PLANNER)
//                .email("planner@manutrack.com").password(passwordEncoder.encode("planner123")).phone("555-0002").build());
//        User supervisor = userRepo.save(User.builder().name("Bob Supervisor").role(User.Role.SUPERVISOR)
//                .email("supervisor@manutrack.com").password(passwordEncoder.encode("supervisor123")).phone("555-0003").build());
//        User inventoryMgr = userRepo.save(User.builder().name("Carol Inventory").role(User.Role.INVENTORY)
//                .email("inventory@manutrack.com").password(passwordEncoder.encode("inventory123")).phone("555-0004").build());
//        User procurement = userRepo.save(User.builder().name("Dave Procurement").role(User.Role.PROCUREMENT)
//                .email("procurement@manutrack.com").password(passwordEncoder.encode("procurement123")).phone("555-0005").build());
//        User logistics = userRepo.save(User.builder().name("Eve Logistics").role(User.Role.LOGISTICS)
//                .email("logistics@manutrack.com").password(passwordEncoder.encode("logistics123")).phone("555-0006").build());
//
//        // Machines
//        Machine m1 = machineRepo.save(Machine.builder().plantId(1L).name("CNC Machine A").capacity(500).status(Machine.Status.ACTIVE).build());
//        Machine m2 = machineRepo.save(Machine.builder().plantId(1L).name("Assembly Robot B").capacity(300).status(Machine.Status.ACTIVE).build());
//        Machine m3 = machineRepo.save(Machine.builder().plantId(1L).name("Press Machine C").capacity(200).status(Machine.Status.MAINTENANCE).build());
//        Machine m4 = machineRepo.save(Machine.builder().plantId(2L).name("Welding Station D").capacity(150).status(Machine.Status.IDLE).build());
//
//        // Production Plans
//        ProductionPlan plan1 = planRepo.save(ProductionPlan.builder().plantId(1L).planName("Q1 Engine Parts Run")
//                .startDate(LocalDate.now().minusDays(10)).endDate(LocalDate.now().plusDays(20))
//                .targetUnits(5000).status(ProductionPlan.Status.IN_PROGRESS).build());
//        ProductionPlan plan2 = planRepo.save(ProductionPlan.builder().plantId(1L).planName("Q2 Chassis Production")
//                .startDate(LocalDate.now().plusDays(30)).endDate(LocalDate.now().plusDays(90))
//                .targetUnits(3000).status(ProductionPlan.Status.PLANNED).build());
//        ProductionPlan plan3 = planRepo.save(ProductionPlan.builder().plantId(2L).planName("Transmission Assembly")
//                .startDate(LocalDate.now().minusDays(60)).endDate(LocalDate.now().minusDays(5))
//                .targetUnits(2000).status(ProductionPlan.Status.COMPLETED).build());
//
//        // Work Orders
//        workOrderRepo.save(WorkOrder.builder().plan(plan1).machine(m1).productId("PRD-ENG-001")
//                .quantity(500).scheduledStart(LocalDateTime.now().minusDays(5))
//                .scheduledEnd(LocalDateTime.now().plusDays(5)).actualStart(LocalDateTime.now().minusDays(5))
//                .status(WorkOrder.Status.IN_PROGRESS).build());
//        workOrderRepo.save(WorkOrder.builder().plan(plan1).machine(m2).productId("PRD-ENG-002")
//                .quantity(300).scheduledStart(LocalDateTime.now()).scheduledEnd(LocalDateTime.now().plusDays(10))
//                .status(WorkOrder.Status.PENDING).build());
//        workOrderRepo.save(WorkOrder.builder().plan(plan1).machine(m3).productId("PRD-ENG-003")
//                .quantity(200).scheduledStart(LocalDateTime.now().minusDays(2))
//                .scheduledEnd(LocalDateTime.now().plusDays(3)).status(WorkOrder.Status.HALTED).build());
//        workOrderRepo.save(WorkOrder.builder().plan(plan3).machine(m4).productId("PRD-TRANS-001")
//                .quantity(1000).scheduledStart(LocalDateTime.now().minusDays(60))
//                .scheduledEnd(LocalDateTime.now().minusDays(10)).actualStart(LocalDateTime.now().minusDays(60))
//                .actualEnd(LocalDateTime.now().minusDays(8)).status(WorkOrder.Status.COMPLETED).build());
//
//        // Inventory
//        InventoryItem steel = itemRepo.save(InventoryItem.builder().itemType(InventoryItem.ItemType.RAW_MATERIAL)
//                .description("Steel Rods Grade A").unitOfMeasure("KG").currentStock(5000.0).reorderLevel(1000.0).warehouseId(1L).build());
//        InventoryItem aluminum = itemRepo.save(InventoryItem.builder().itemType(InventoryItem.ItemType.RAW_MATERIAL)
//                .description("Aluminum Sheets 3mm").unitOfMeasure("KG").currentStock(800.0).reorderLevel(1000.0).warehouseId(1L).build());
//        itemRepo.save(InventoryItem.builder().itemType(InventoryItem.ItemType.RAW_MATERIAL)
//                .description("Hydraulic Oil Type B").unitOfMeasure("LITRE").currentStock(0.0).reorderLevel(200.0).warehouseId(1L).build());
//        itemRepo.save(InventoryItem.builder().itemType(InventoryItem.ItemType.FINISHED_GOOD)
//                .description("Engine Block V6").unitOfMeasure("UNIT").currentStock(250.0).reorderLevel(50.0).warehouseId(2L).build());
//        itemRepo.save(InventoryItem.builder().itemType(InventoryItem.ItemType.FINISHED_GOOD)
//                .description("Transmission Unit Auto").unitOfMeasure("UNIT").currentStock(120.0).reorderLevel(30.0).warehouseId(2L).build());
//
//        // Vendors
//        Vendor v1 = vendorRepo.save(Vendor.builder().name("SteelCo Supplies").contactInfo("contact@steelco.com").rating(4.5).status(Vendor.Status.ACTIVE).build());
//        Vendor v2 = vendorRepo.save(Vendor.builder().name("AlumaTech Ltd").contactInfo("sales@alumatech.com").rating(4.2).status(Vendor.Status.ACTIVE).build());
//        Vendor v3 = vendorRepo.save(Vendor.builder().name("ChemLube International").contactInfo("orders@chemlube.com").rating(3.8).status(Vendor.Status.ACTIVE).build());
//
//        // Purchase Orders
//        PurchaseOrder po1 = poRepo.save(PurchaseOrder.builder().vendor(v1).itemId(steel.getItemId())
//                .quantity(2000.0).unitPrice(3.50).orderDate(LocalDate.now().minusDays(5))
//                .expectedDeliveryDate(LocalDate.now().plusDays(10)).status(PurchaseOrder.Status.OPEN).build());
//        PurchaseOrder po2 = poRepo.save(PurchaseOrder.builder().vendor(v2).itemId(aluminum.getItemId())
//                .quantity(500.0).unitPrice(8.00).orderDate(LocalDate.now().minusDays(3))
//                .expectedDeliveryDate(LocalDate.now().plusDays(7)).status(PurchaseOrder.Status.OPEN).build());
//        PurchaseOrder po3 = poRepo.save(PurchaseOrder.builder().vendor(v3).itemId(3L)
//                .quantity(300.0).unitPrice(12.50).orderDate(LocalDate.now().minusDays(20))
//                .expectedDeliveryDate(LocalDate.now().minusDays(5)).status(PurchaseOrder.Status.DELIVERED).build());
//
//        // Invoices
//        invoiceRepo.save(Invoice.builder().purchaseOrder(po1).amount(7000.0)
//                .issueDate(LocalDate.now().minusDays(4)).dueDate(LocalDate.now().plusDays(26)).status(Invoice.Status.PENDING).build());
//        invoiceRepo.save(Invoice.builder().purchaseOrder(po2).amount(4000.0)
//                .issueDate(LocalDate.now().minusDays(2)).dueDate(LocalDate.now().plusDays(28)).status(Invoice.Status.PENDING).build());
//        invoiceRepo.save(Invoice.builder().purchaseOrder(po3).amount(3750.0)
//                .issueDate(LocalDate.now().minusDays(18)).dueDate(LocalDate.now().minusDays(3)).status(Invoice.Status.OVERDUE).build());
//
//        // Carriers
//        Carrier c1 = carrierRepo.save(Carrier.builder().name("FastFreight Express").contactInfo("ops@fastfreight.com").rating(4.7).status(Carrier.Status.ACTIVE).build());
//        Carrier c2 = carrierRepo.save(Carrier.builder().name("GlobalShip Logistics").contactInfo("dispatch@globalship.com").rating(4.1).status(Carrier.Status.ACTIVE).build());
//
//        // Routes
//        routeRepo.save(Route.builder().origin("Plant A - Detroit").destination("Warehouse B - Chicago").distance(450.0).estimatedTimeHours(6).build());
//        routeRepo.save(Route.builder().origin("Warehouse B - Chicago").destination("Customer Hub - New York").distance(1200.0).estimatedTimeHours(16).build());
//        routeRepo.save(Route.builder().origin("Plant C - Dallas").destination("Port Terminal - Houston").distance(240.0).estimatedTimeHours(4).build());
//
//        // Shipments
//        shipmentRepo.save(Shipment.builder().carrier(c1).originWarehouseId(1L).destination("Chicago Distribution Hub")
//                .scheduledDate(LocalDate.now().plusDays(3)).status(Shipment.Status.SCHEDULED).build());
//        shipmentRepo.save(Shipment.builder().carrier(c2).originWarehouseId(2L).destination("New York Customer Hub")
//                .scheduledDate(LocalDate.now().minusDays(2)).actualDispatchDate(LocalDate.now().minusDays(2))
//                .status(Shipment.Status.IN_TRANSIT).build());
//        shipmentRepo.save(Shipment.builder().carrier(c1).originWarehouseId(1L).destination("Dallas Assembly Plant")
//                .scheduledDate(LocalDate.now().minusDays(10)).actualDispatchDate(LocalDate.now().minusDays(10))
//                .deliveryDate(LocalDate.now().minusDays(8)).status(Shipment.Status.DELIVERED).build());
//        shipmentRepo.save(Shipment.builder().carrier(c2).originWarehouseId(2L).destination("Houston Port Terminal")
//                .scheduledDate(LocalDate.now().minusDays(5)).status(Shipment.Status.DELAYED).build());
//
//        // Notifications
//        notifRepo.save(Notification.builder().userId(inventoryMgr.getUserId()).message("Hydraulic Oil Type B is OUT OF STOCK. Immediate reorder required.").category(Notification.Category.INVENTORY).build());
//        notifRepo.save(Notification.builder().userId(inventoryMgr.getUserId()).message("Aluminum Sheets 3mm stock is below reorder level (800 KG remaining).").category(Notification.Category.INVENTORY).build());
//        notifRepo.save(Notification.builder().userId(supervisor.getUserId()).message("Work Order WO-3 for Press Machine C has been HALTED. Maintenance required.").category(Notification.Category.MACHINE).build());
//        notifRepo.save(Notification.builder().userId(logistics.getUserId()).message("Shipment to Houston Port Terminal is DELAYED. Please investigate.").category(Notification.Category.SHIPMENT).build());
//        notifRepo.save(Notification.builder().userId(procurement.getUserId()).message("Invoice for PO-3 (ChemLube International) is OVERDUE. Payment pending.").category(Notification.Category.PROCUREMENT).build());
//        notifRepo.save(Notification.builder().userId(planner.getUserId()).message("Production Plan 'Q1 Engine Parts Run' is IN PROGRESS. 3 active work orders.").category(Notification.Category.WORK_ORDER).build());
//
//        log.info("Demo data seeded successfully! Login credentials:");
//        log.info("Admin:       admin@manutrack.com / admin123");
//        log.info("Planner:     planner@manutrack.com / planner123");
//        log.info("Supervisor:  supervisor@manutrack.com / supervisor123");
//        log.info("Inventory:   inventory@manutrack.com / inventory123");
//        log.info("Procurement: procurement@manutrack.com / procurement123");
//        log.info("Logistics:   logistics@manutrack.com / logistics123");
//    }
//}

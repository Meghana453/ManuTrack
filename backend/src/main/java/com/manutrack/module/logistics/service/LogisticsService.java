
package com.manutrack.module.logistics.service;

import com.manutrack.automation.AutomationService;
import com.manutrack.exception.ResourceNotFoundException;
import com.manutrack.module.logistics.dto.LogisticsDtos;
import com.manutrack.module.logistics.entity.Carrier;
import com.manutrack.module.logistics.entity.Route;
import com.manutrack.module.logistics.entity.Shipment;
import com.manutrack.module.logistics.mapper.LogisticsMapper;
import com.manutrack.module.logistics.repository.CarrierRepository;
import com.manutrack.module.logistics.repository.RouteRepository;
import com.manutrack.module.logistics.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service @RequiredArgsConstructor @Transactional @Slf4j
public class LogisticsService {

    private final CarrierRepository carrierRepo;
    private final RouteRepository routeRepo;
    private final ShipmentRepository shipmentRepo;
    private final LogisticsMapper mapper;
    private final AutomationService automationService;

    // ── Carriers ──────────────────────────────────────────

    public LogisticsDtos.CarrierResponse createCarrier(LogisticsDtos.CreateCarrierRequest req) {
        Carrier c = Carrier.builder().name(req.getName()).contactInfo(req.getContactInfo())
                .rating(req.getRating() != null ? req.getRating() : 0.0)
                .status(req.getStatus() != null ? req.getStatus() : Carrier.Status.ACTIVE).build();
        Carrier saved = carrierRepo.save(c);
        automationService.notifyAllRoles(
                String.format("🚛 New Carrier '%s' registered | Contact: %s | Rating: %.1f | Status: %s",
                        saved.getName(), saved.getContactInfo() != null ? saved.getContactInfo() : "N/A",
                        saved.getRating() != null ? saved.getRating() : 0.0, saved.getStatus()),
                com.manutrack.module.notification.entity.Notification.Category.SHIPMENT);
        return mapper.toCarrierResponse(saved);
    }
    @Transactional(readOnly=true) public List<LogisticsDtos.CarrierResponse> getAllCarriers() { return carrierRepo.findAll().stream().map(mapper::toCarrierResponse).toList(); }
    @Transactional(readOnly=true) public LogisticsDtos.CarrierResponse getCarrierById(Long id) { return mapper.toCarrierResponse(findCarrierById(id)); }
    public LogisticsDtos.CarrierResponse updateCarrier(Long id, LogisticsDtos.UpdateCarrierRequest req) { Carrier c = findCarrierById(id); mapper.updateCarrier(req, c); return mapper.toCarrierResponse(carrierRepo.save(c)); }
    public void deleteCarrier(Long id) { findCarrierById(id); carrierRepo.deleteById(id); }

    // ── Routes ────────────────────────────────────────────

    public LogisticsDtos.RouteResponse createRoute(LogisticsDtos.CreateRouteRequest req) {
        Route r = Route.builder().origin(req.getOrigin()).destination(req.getDestination())
                .distance(req.getDistance()).estimatedTimeHours(req.getEstimatedTimeHours()).build();
        return mapper.toRouteResponse(routeRepo.save(r));
    }
    @Transactional(readOnly=true) public List<LogisticsDtos.RouteResponse> getAllRoutes() { return routeRepo.findAll().stream().map(mapper::toRouteResponse).toList(); }
    @Transactional(readOnly=true) public LogisticsDtos.RouteResponse getRouteById(Long id) { return mapper.toRouteResponse(findRouteById(id)); }
    public LogisticsDtos.RouteResponse updateRoute(Long id, LogisticsDtos.UpdateRouteRequest req) { Route r = findRouteById(id); mapper.updateRoute(req, r); return mapper.toRouteResponse(routeRepo.save(r)); }
    public void deleteRoute(Long id) { findRouteById(id); routeRepo.deleteById(id); }

    // ── Shipments ─────────────────────────────────────────

    public LogisticsDtos.ShipmentResponse createShipment(LogisticsDtos.CreateShipmentRequest req) {
        Shipment s = Shipment.builder().destination(req.getDestination())
                .originWarehouseId(req.getOriginWarehouseId())
                .scheduledDate(req.getScheduledDate()).status(Shipment.Status.SCHEDULED).build();
        if (req.getCarrierId() != null) s.setCarrier(findCarrierById(req.getCarrierId()));
        Shipment saved = shipmentRepo.save(s);
        automationService.notifyAllRoles(
                String.format("📦 New Shipment #%d scheduled | Destination: %s | Carrier: %s | Scheduled: %s",
                        saved.getShipmentId(), saved.getDestination(),
                        saved.getCarrier() != null ? saved.getCarrier().getName() : "Not assigned",
                        saved.getScheduledDate() != null ? saved.getScheduledDate().toString() : "N/A"),
                com.manutrack.module.notification.entity.Notification.Category.SHIPMENT);
        return mapper.toShipmentResponse(saved);
    }
    @Transactional(readOnly=true) public List<LogisticsDtos.ShipmentResponse> getAllShipments() { return shipmentRepo.findAll().stream().map(mapper::toShipmentResponse).toList(); }
    @Transactional(readOnly=true) public LogisticsDtos.ShipmentResponse getShipmentById(Long id) { return mapper.toShipmentResponse(findShipmentById(id)); }
    @Transactional(readOnly=true) public List<LogisticsDtos.ShipmentResponse> getShipmentsByStatus(Shipment.Status status) { return shipmentRepo.findByStatus(status).stream().map(mapper::toShipmentResponse).toList(); }

    public LogisticsDtos.ShipmentResponse updateShipment(Long id, LogisticsDtos.UpdateShipmentRequest req) {
        Shipment s = findShipmentById(id);
        Shipment.Status prev = s.getStatus();
        mapper.updateShipment(req, s);
        if (req.getCarrierId() != null) s.setCarrier(findCarrierById(req.getCarrierId()));
        s = shipmentRepo.save(s);

        // ── AUTOMATION: Shipment status changes ──
        if (req.getStatus() != null && req.getStatus() != prev) {
            switch (req.getStatus()) {
                case IN_TRANSIT -> automationService.onShipmentDispatched(s);
                case DELIVERED  -> automationService.onShipmentDelivered(s);
                case DELAYED    -> automationService.onShipmentDelayed(s);
                default -> {}
            }
        }
        return mapper.toShipmentResponse(s);
    }
    public void deleteShipment(Long id) { findShipmentById(id); shipmentRepo.deleteById(id); }

    private Carrier findCarrierById(Long id) { return carrierRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Carrier","id",id)); }
    private Route findRouteById(Long id) { return routeRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Route","id",id)); }
    private Shipment findShipmentById(Long id) { return shipmentRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Shipment","id",id)); }
}

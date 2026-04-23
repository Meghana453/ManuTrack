package com.manutrack.module.procurement.mapper;

import com.manutrack.module.procurement.dto.ProcurementDtos;
import com.manutrack.module.procurement.entity.Invoice;
import com.manutrack.module.procurement.entity.PurchaseOrder;
import com.manutrack.module.procurement.entity.Vendor;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProcurementMapper {
    ProcurementDtos.VendorResponse toVendorResponse(Vendor vendor);
    void updateVendor(ProcurementDtos.UpdateVendorRequest req, @MappingTarget Vendor vendor);

    @Mapping(target = "vendorId", source = "vendor.vendorId")
    @Mapping(target = "vendorName", source = "vendor.name")
    @Mapping(target = "totalAmount", expression = "java(po.getQuantity() * po.getUnitPrice())")
    ProcurementDtos.POResponse toPOResponse(PurchaseOrder po);

    void updatePO(ProcurementDtos.UpdatePORequest req, @MappingTarget PurchaseOrder po);

    @Mapping(target = "poId", source = "purchaseOrder.poId")
    @Mapping(target = "vendorId", source = "purchaseOrder.vendor.vendorId")
    @Mapping(target = "vendorName", source = "purchaseOrder.vendor.name")
    ProcurementDtos.InvoiceResponse toInvoiceResponse(Invoice invoice);

    void updateInvoice(ProcurementDtos.UpdateInvoiceRequest req, @MappingTarget Invoice invoice);
}

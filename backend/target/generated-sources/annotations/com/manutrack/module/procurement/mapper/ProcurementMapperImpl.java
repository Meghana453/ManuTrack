package com.manutrack.module.procurement.mapper;

import com.manutrack.module.procurement.dto.ProcurementDtos;
import com.manutrack.module.procurement.entity.Invoice;
import com.manutrack.module.procurement.entity.PurchaseOrder;
import com.manutrack.module.procurement.entity.Vendor;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-26T18:22:21+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.2 (Oracle Corporation)"
)
@Component
public class ProcurementMapperImpl implements ProcurementMapper {

    @Override
    public ProcurementDtos.VendorResponse toVendorResponse(Vendor vendor) {
        if ( vendor == null ) {
            return null;
        }

        ProcurementDtos.VendorResponse vendorResponse = new ProcurementDtos.VendorResponse();

        vendorResponse.setVendorId( vendor.getVendorId() );
        vendorResponse.setName( vendor.getName() );
        vendorResponse.setContactInfo( vendor.getContactInfo() );
        vendorResponse.setRating( vendor.getRating() );
        vendorResponse.setStatus( vendor.getStatus() );
        vendorResponse.setCreatedAt( vendor.getCreatedAt() );

        return vendorResponse;
    }

    @Override
    public void updateVendor(ProcurementDtos.UpdateVendorRequest req, Vendor vendor) {
        if ( req == null ) {
            return;
        }

        if ( req.getName() != null ) {
            vendor.setName( req.getName() );
        }
        if ( req.getContactInfo() != null ) {
            vendor.setContactInfo( req.getContactInfo() );
        }
        if ( req.getRating() != null ) {
            vendor.setRating( req.getRating() );
        }
        if ( req.getStatus() != null ) {
            vendor.setStatus( req.getStatus() );
        }
    }

    @Override
    public ProcurementDtos.POResponse toPOResponse(PurchaseOrder po) {
        if ( po == null ) {
            return null;
        }

        ProcurementDtos.POResponse pOResponse = new ProcurementDtos.POResponse();

        pOResponse.setVendorId( poVendorVendorId( po ) );
        pOResponse.setVendorName( poVendorName( po ) );
        pOResponse.setPoId( po.getPoId() );
        pOResponse.setItemId( po.getItemId() );
        pOResponse.setQuantity( po.getQuantity() );
        pOResponse.setUnitPrice( po.getUnitPrice() );
        pOResponse.setOrderDate( po.getOrderDate() );
        pOResponse.setExpectedDeliveryDate( po.getExpectedDeliveryDate() );
        pOResponse.setStatus( po.getStatus() );
        pOResponse.setCreatedAt( po.getCreatedAt() );

        pOResponse.setTotalAmount( po.getQuantity() * po.getUnitPrice() );

        return pOResponse;
    }

    @Override
    public void updatePO(ProcurementDtos.UpdatePORequest req, PurchaseOrder po) {
        if ( req == null ) {
            return;
        }

        if ( req.getQuantity() != null ) {
            po.setQuantity( req.getQuantity() );
        }
        if ( req.getUnitPrice() != null ) {
            po.setUnitPrice( req.getUnitPrice() );
        }
        if ( req.getExpectedDeliveryDate() != null ) {
            po.setExpectedDeliveryDate( req.getExpectedDeliveryDate() );
        }
        if ( req.getStatus() != null ) {
            po.setStatus( req.getStatus() );
        }
    }

    @Override
    public ProcurementDtos.InvoiceResponse toInvoiceResponse(Invoice invoice) {
        if ( invoice == null ) {
            return null;
        }

        ProcurementDtos.InvoiceResponse invoiceResponse = new ProcurementDtos.InvoiceResponse();

        invoiceResponse.setPoId( invoicePurchaseOrderPoId( invoice ) );
        invoiceResponse.setVendorId( invoicePurchaseOrderVendorVendorId( invoice ) );
        invoiceResponse.setVendorName( invoicePurchaseOrderVendorName( invoice ) );
        invoiceResponse.setInvoiceId( invoice.getInvoiceId() );
        invoiceResponse.setAmount( invoice.getAmount() );
        invoiceResponse.setIssueDate( invoice.getIssueDate() );
        invoiceResponse.setDueDate( invoice.getDueDate() );
        invoiceResponse.setStatus( invoice.getStatus() );
        invoiceResponse.setCreatedAt( invoice.getCreatedAt() );

        return invoiceResponse;
    }

    @Override
    public void updateInvoice(ProcurementDtos.UpdateInvoiceRequest req, Invoice invoice) {
        if ( req == null ) {
            return;
        }

        if ( req.getAmount() != null ) {
            invoice.setAmount( req.getAmount() );
        }
        if ( req.getDueDate() != null ) {
            invoice.setDueDate( req.getDueDate() );
        }
        if ( req.getStatus() != null ) {
            invoice.setStatus( req.getStatus() );
        }
    }

    private Long poVendorVendorId(PurchaseOrder purchaseOrder) {
        if ( purchaseOrder == null ) {
            return null;
        }
        Vendor vendor = purchaseOrder.getVendor();
        if ( vendor == null ) {
            return null;
        }
        Long vendorId = vendor.getVendorId();
        if ( vendorId == null ) {
            return null;
        }
        return vendorId;
    }

    private String poVendorName(PurchaseOrder purchaseOrder) {
        if ( purchaseOrder == null ) {
            return null;
        }
        Vendor vendor = purchaseOrder.getVendor();
        if ( vendor == null ) {
            return null;
        }
        String name = vendor.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private Long invoicePurchaseOrderPoId(Invoice invoice) {
        if ( invoice == null ) {
            return null;
        }
        PurchaseOrder purchaseOrder = invoice.getPurchaseOrder();
        if ( purchaseOrder == null ) {
            return null;
        }
        Long poId = purchaseOrder.getPoId();
        if ( poId == null ) {
            return null;
        }
        return poId;
    }

    private Long invoicePurchaseOrderVendorVendorId(Invoice invoice) {
        if ( invoice == null ) {
            return null;
        }
        PurchaseOrder purchaseOrder = invoice.getPurchaseOrder();
        if ( purchaseOrder == null ) {
            return null;
        }
        Vendor vendor = purchaseOrder.getVendor();
        if ( vendor == null ) {
            return null;
        }
        Long vendorId = vendor.getVendorId();
        if ( vendorId == null ) {
            return null;
        }
        return vendorId;
    }

    private String invoicePurchaseOrderVendorName(Invoice invoice) {
        if ( invoice == null ) {
            return null;
        }
        PurchaseOrder purchaseOrder = invoice.getPurchaseOrder();
        if ( purchaseOrder == null ) {
            return null;
        }
        Vendor vendor = purchaseOrder.getVendor();
        if ( vendor == null ) {
            return null;
        }
        String name = vendor.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}

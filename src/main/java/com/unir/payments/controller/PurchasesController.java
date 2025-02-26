package com.unir.payments.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.unir.payments.controller.model.PurchaseDto;
import com.unir.payments.controller.model.CreatePurchaseRequest;
import com.unir.payments.data.model.Purchase;
import com.unir.payments.service.PurchasesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Purchases Controller", description = "Microservicio encargado de registrar y gestionar compras.")
public class PurchasesController {

    private final PurchasesService service;

    @GetMapping("/purchases")
    @Operation(
            operationId = "Obtener compras",
            description = "Operación de lectura",
            summary = "Se devuelve una lista de todas las compras registradas.")
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Purchase.class)))
    public ResponseEntity<List<Purchase>> getPurchases(
            @RequestHeader Map<String, String> headers,
            @Parameter(name = "bookIsbn", description = "ISBN del libro comprado", example = "9780307389732", required = false)
            @RequestParam(required = false) String bookIsbn,
            @Parameter(name = "buyer", description = "Información del comprador (por ejemplo, email)", example = "juan.perez@example.com", required = false)
            @RequestParam(required = false) String buyer,
            @Parameter(name = "status", description = "Estado de la compra (e.g., COMPLETED, PENDING)", example = "COMPLETED", required = false)
            @RequestParam(required = false) String status) {

        log.info("headers: {}", headers);
        List<Purchase> purchases = service.getPurchases(bookIsbn, buyer, status);
        return ResponseEntity.ok(purchases != null ? purchases : Collections.emptyList());
    }

    @GetMapping("/purchases/{purchaseId}")
    @Operation(
            operationId = "Obtener una compra",
            description = "Operación de lectura",
            summary = "Se devuelve una compra a partir de su identificador.")
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Purchase.class)))
    @ApiResponse(
            responseCode = "404",
            description = "No se ha encontrado la compra con el identificador indicado.")
    public ResponseEntity<Purchase> getPurchase(@PathVariable String purchaseId) {
        log.info("Request received for purchase {}", purchaseId);
        Purchase purchase = service.getPurchase(purchaseId);
        return purchase != null ? ResponseEntity.ok(purchase) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/purchases/{purchaseId}")
    @Operation(
            operationId = "Eliminar una compra",
            description = "Operación de escritura",
            summary = "Se elimina una compra a partir de su identificador.")
    @ApiResponse(
            responseCode = "200",
            description = "Compra eliminada correctamente.")
    @ApiResponse(
            responseCode = "404",
            description = "No se ha encontrado la compra con el identificador indicado.")
    public ResponseEntity<Void> deletePurchase(@PathVariable String purchaseId) {
        boolean removed = service.removePurchase(purchaseId);
        return removed ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/purchases")
    @Operation(
            operationId = "Registrar una compra",
            description = "Operación de escritura",
            summary = "Se registra una compra a partir de sus datos.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la compra a registrar.",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreatePurchaseRequest.class))))
    @ApiResponse(
            responseCode = "201",
            description = "Compra registrada exitosamente.")
    @ApiResponse(
            responseCode = "400",
            description = "Datos incorrectos introducidos.")
    public ResponseEntity<Purchase> addPurchase(@RequestBody CreatePurchaseRequest request) {
        Purchase createdPurchase = service.createPurchase(request);
        return createdPurchase != null ? ResponseEntity.status(HttpStatus.CREATED).body(createdPurchase)
                : ResponseEntity.badRequest().build();
    }

    @PatchMapping("/purchases/{purchaseId}")
    @Operation(
            operationId = "Modificar parcialmente una compra",
            description = "RFC 7386. Operación de escritura",
            summary = "Se modifica parcialmente una compra.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la compra a modificar.",
                    required = true,
                    content = @Content(mediaType = "application/merge-patch+json", schema = @Schema(implementation = String.class))))
    @ApiResponse(
            responseCode = "200",
            description = "Compra modificada exitosamente.")
    @ApiResponse(
            responseCode = "400",
            description = "Compra inválida o datos incorrectos introducidos.")
    public ResponseEntity<Purchase> patchPurchase(@PathVariable String purchaseId, @RequestBody String patchBody) {
        Purchase patchedPurchase = service.updatePurchase(purchaseId, patchBody);
        return patchedPurchase != null ? ResponseEntity.ok(patchedPurchase) : ResponseEntity.badRequest().build();
    }

    @PutMapping("/purchases/{purchaseId}")
    @Operation(
            operationId = "Modificar totalmente una compra",
            description = "Operación de escritura",
            summary = "Se modifica totalmente una compra.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la compra a actualizar.",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PurchaseDto.class))))
    @ApiResponse(
            responseCode = "200",
            description = "Compra actualizada exitosamente.")
    @ApiResponse(
            responseCode = "404",
            description = "Compra no encontrada.")
    public ResponseEntity<Purchase> updatePurchase(@PathVariable String purchaseId, @RequestBody PurchaseDto body) {
        Purchase updatedPurchase = service.updatePurchase(purchaseId, body);
        return updatedPurchase != null ? ResponseEntity.ok(updatedPurchase) : ResponseEntity.notFound().build();
    }
}

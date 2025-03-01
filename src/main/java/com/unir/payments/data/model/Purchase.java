package com.unir.payments.data.model;

import com.unir.payments.controller.model.PurchaseDto;
import com.unir.payments.data.utils.Consts;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "purchases")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Purchase {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// ISBN del libro adquirido
	@Column(name = Consts.BOOK_ID, nullable = false)
	private String bookId;

	// Fecha y hora de la compra
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Column(name = Consts.PURCHASE_DATE, nullable = false)
	private LocalDateTime purchaseDate;

	// Cantidad de ejemplares adquiridos
	@Column(name = Consts.QUANTITY, nullable = false)
	private Integer quantity;

	// Información del comprador (por ejemplo, email)
	@Column(name = Consts.BUYER, nullable = false)
	private String buyer;

	// Estado de la compra (por ejemplo, "COMPLETED", "PENDING", etc.)
	@Column(name = Consts.STATUS, nullable = false)
	private String status;

	// Método para actualizar la entidad a partir de un PurchaseDto
	public void update(PurchaseDto purchaseDto) {
		this.bookId = purchaseDto.getBookId();
		this.purchaseDate = purchaseDto.getPurchaseDate();
		this.quantity = purchaseDto.getQuantity();
		this.buyer = purchaseDto.getBuyer();
		this.status = purchaseDto.getStatus();
	}
}

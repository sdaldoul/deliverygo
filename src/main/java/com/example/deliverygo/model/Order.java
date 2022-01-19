package com.example.deliverygo.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Order")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

	@Id
	private String id;
	private LocalDateTime orderCreationDateTime;
	private LocalDateTime orderUpdateDateTime;
	private UserProfile user;
	private String productName;
	private LocalDate whenYouNeedIt;
	private String readyToPay;
	private String cityToCollect;
	private List<Proposal> proposals;

}

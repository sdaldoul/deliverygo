package com.example.deliverygo.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proposal {

	private UserProfile user;
	private LocalDate whenYouCanDeliverIt;
	private String wantToGetPaid;
	private String proposalCityToCollect;
	private LocalDateTime proposalCreationDateTime;
	private LocalDateTime proposalUpdateDateTime;

}

package com.example.deliverygo.entity;

import com.example.deliverygo.model.UserProfile;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Proposal")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proposal {

	@Id
	private String id;
	private UserProfile user;
	private LocalDate whenYouCanDeliverIt;
	private String wantToGetPaid;
	private String proposalCityToCollect;
	private LocalDateTime proposalCreationDateTime;
	private LocalDateTime proposalUpdateDateTime;

}

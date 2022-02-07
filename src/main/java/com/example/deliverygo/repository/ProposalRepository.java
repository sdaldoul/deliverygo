package com.example.deliverygo.repository;

import com.example.deliverygo.entity.Proposal;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProposalRepository extends ReactiveMongoRepository<Proposal, String> {


}

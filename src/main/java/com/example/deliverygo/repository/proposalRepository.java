package com.example.deliverygo.repository;

import com.example.deliverygo.model.Proposal;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface proposalRepository extends ReactiveMongoRepository<Proposal, String> {


}

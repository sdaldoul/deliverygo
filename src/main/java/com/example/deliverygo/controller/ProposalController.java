package com.example.deliverygo.controller;


import com.example.deliverygo.model.ProductEvent;
import com.example.deliverygo.entity.Proposal;
import com.example.deliverygo.repository.ProposalRepository;
import java.time.Duration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/proposals")
public class ProposalController {

    private ProposalRepository repository;

    public ProposalController(ProposalRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public Flux<Proposal> getAllProducts() {
        return repository.findAll();
    }

    @GetMapping("{id}")
    public Mono<ResponseEntity<Proposal>> getProposal(@PathVariable String id) {
        return repository.findById(id)
                .map(product -> ResponseEntity.ok(product))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Proposal> saveProposal(@RequestBody Proposal product) {
        return repository.save(product);
    }

    @PutMapping("{id}")
    public Mono<ResponseEntity<Proposal>> updateProposal(@PathVariable(value = "id") String id,
                                                       @RequestBody Proposal proposal) {
        return repository.findById(id)
                .flatMap(existingProposal -> {
                    existingProposal.setUser(proposal.getUser());
                    return repository.save(existingProposal);
                })
                .map(updateProduct -> ResponseEntity.ok(updateProduct))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    public Mono<ResponseEntity<Void>> deleteProposal(@PathVariable(value = "id") String id) {
        return repository.findById(id)
                .flatMap(existingProposal ->
                        repository.delete(existingProposal)
                                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping
    public Mono<Void> deleteAllProposal() {
        return repository.deleteAll();
    }

    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ProductEvent> getProductEvents() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(val ->
                        new ProductEvent(val, "Product Event")
                );
    }
}

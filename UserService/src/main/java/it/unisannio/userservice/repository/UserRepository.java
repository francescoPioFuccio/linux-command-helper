package it.unisannio.userservice.repository;

import it.unisannio.userservice.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<Object> findByEmail(String email);
    Optional<Object> findByUsername(String username);
    Optional<User> findByProviderAndProviderId(String provider, String providerId);
}

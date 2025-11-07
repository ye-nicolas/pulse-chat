package com.nicolas.pulse.service.usecase;

import com.github.f4b6a3.ulid.UlidCreator;
import com.nicolas.pulse.entity.domain.User;
import com.nicolas.pulse.service.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CreateUserUseCase {
    private final UserRepository userRepository;

    public CreateUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<Output> execute(Mono<Input> input) {
        return input.flatMap(i -> userRepository.existsByName(i.getName())
                        .flatMap(exists -> {
                            if (exists) {
                                return Mono.error(new IllegalStateException("User name already exists."));
                            }
                            return Mono.just(i);
                        }))
                .flatMap(this::createUser).map(user -> new Output(user.getId()));
    }

    private Mono<User> createUser(Input input) {
        String id = UlidCreator.getMonotonicUlid().toString();
        User user = User.builder()
                .id(id)
                .name(input.getName())
                .showName(input.getShowName())
                .password(input.getPassword())
                .isActive(false)
                .remark(input.getRemark())
                .createdBy(id)
                .updatedBy(id)
                .build();
        return userRepository.create(user);
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class Input {
        private String name;
        private String showName;
        private String password;
        private String remark;
    }

    @Data
    @AllArgsConstructor
    public static class Output {
        private String userId;
    }
}

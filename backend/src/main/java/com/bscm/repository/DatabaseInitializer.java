package com.bscm.repository;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer {
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final DiagnosisRecordRepository diagnosisRecordRepository;
    
    @Bean
    ApplicationRunner init() {
        return args -> {
            userRepository.count();
            chatMessageRepository.count();
            diagnosisRecordRepository.count();
            System.out.println("数据库已初始化完成");
        };
    }
}

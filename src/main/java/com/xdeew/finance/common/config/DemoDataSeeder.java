package com.xdeew.finance.common.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.xdeew.finance.account.model.Account;
import com.xdeew.finance.account.model.AccountType;
import com.xdeew.finance.account.repository.AccountRepository;
import com.xdeew.finance.category.model.Category;
import com.xdeew.finance.category.repository.CategoryRepository;
import com.xdeew.finance.transaction.model.Transaction;
import com.xdeew.finance.transaction.model.TransactionType;
import com.xdeew.finance.transaction.repository.TransactionRepository;
import com.xdeew.finance.user.model.User;
import com.xdeew.finance.user.model.UserRole;
import com.xdeew.finance.user.repository.UserRepository;

@Configuration
@Profile("demo")
public class DemoDataSeeder {

    @Bean
    CommandLineRunner seedDemoData(UserRepository userRepository,
            AccountRepository accountRepository,
            CategoryRepository categoryRepository,
            TransactionRepository transactionRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmail("demo@example.com").isPresent()) {
                return;
            }

            User user = userRepository.save(User.builder()
                    .email("demo@example.com")
                    .passwordHash(passwordEncoder.encode("password123"))
                    .firstName("Demo")
                    .lastName("User")
                    .role(UserRole.USER)
                    .build());

            Category salary = categoryRepository.save(Category.builder()
                    .name("SALARY")
                    .type(com.xdeew.finance.category.model.CategoryType.INCOME)
                    .user(user)
                    .build());

            Category food = categoryRepository.save(Category.builder()
                    .name("FOOD")
                    .type(com.xdeew.finance.category.model.CategoryType.EXPENSE)
                    .user(user)
                    .build());

            Category rent = categoryRepository.save(Category.builder()
                    .name("RENT")
                    .type(com.xdeew.finance.category.model.CategoryType.EXPENSE)
                    .user(user)
                    .build());

            Account checking = accountRepository.save(Account.builder()
                    .name("Main Checking")
                    .type(AccountType.CHECKING)
                    .balance(new BigDecimal("2500.00"))
                    .currency("EUR")
                    .user(user)
                    .build());

            Account savings = accountRepository.save(Account.builder()
                    .name("Savings")
                    .type(AccountType.SAVINGS)
                    .balance(new BigDecimal("8000.00"))
                    .currency("EUR")
                    .user(user)
                    .build());

            transactionRepository.saveAll(List.of(
                    Transaction.builder()
                            .description("Monthly salary")
                            .amount(new BigDecimal("2200.00"))
                            .type(TransactionType.INCOME)
                            .transactionDate(LocalDateTime.now().minusDays(15))
                            .user(user)
                            .account(checking)
                            .category(salary)
                            .build(),
                    Transaction.builder()
                            .description("Supermarket")
                            .amount(new BigDecimal("85.50"))
                            .type(TransactionType.EXPENSE)
                            .transactionDate(LocalDateTime.now().minusDays(10))
                            .user(user)
                            .account(checking)
                            .category(food)
                            .build(),
                    Transaction.builder()
                            .description("Rent payment")
                            .amount(new BigDecimal("900.00"))
                            .type(TransactionType.EXPENSE)
                            .transactionDate(LocalDateTime.now().minusDays(5))
                            .user(user)
                            .account(checking)
                            .category(rent)
                            .build()
            ));
        };
    }
}

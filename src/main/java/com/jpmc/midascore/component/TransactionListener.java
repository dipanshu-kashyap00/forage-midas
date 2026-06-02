package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TransactionListener {

    static final Logger logger = LoggerFactory.getLogger(TransactionListener.class);

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public TransactionListener(UserRepository userRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @KafkaListener(topics = "${general.kafka-topic}")
    public void listen(Transaction transaction) {
        // Step 1 - find sender and recipient in database
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());

        // Step 2 - validate
        if (sender == null || recipient == null) {
            logger.info("Invalid transaction - user not found: " + transaction);
            return;
        }
        if (sender.getBalance() < transaction.getAmount()) {
            logger.info("Invalid transaction - insufficient balance: " + transaction);
            return;
        }

        // Step 3 - call incentive API
        Incentive incentive = restTemplate.postForObject(
            "http://localhost:8080/incentive",
            transaction,
            Incentive.class
        );
        float incentiveAmount = (incentive != null) ? incentive.getAmount() : 0f;

        // Step 4 - update balances
        sender.setBalance(sender.getBalance() - transaction.getAmount());
        recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentiveAmount);
        userRepository.save(sender);
        userRepository.save(recipient);

        // Step 5 - save transaction record
        TransactionRecord record = new TransactionRecord(sender, recipient, transaction.getAmount(), incentiveAmount);
        transactionRepository.save(record);

        logger.info("Valid transaction saved: " + transaction + " | incentive: " + incentiveAmount);
    }
}

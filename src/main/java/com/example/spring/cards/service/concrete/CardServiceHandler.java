package com.example.spring.cards.service.concrete;

import com.example.spring.cards.dao.entity.CardEntity;
import com.example.spring.cards.dao.repository.CardRepository;
import com.example.spring.cards.exception.NotFoundException;
import com.example.spring.cards.model.request.CreateCardRequest;
import com.example.spring.cards.model.response.CardResponse;
import com.example.spring.cards.service.abstraction.CardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static com.example.spring.cards.mapper.CardMapper.CARD_MAPPER;
import static com.example.spring.cards.model.enums.CardStatus.DELETED;
import static com.example.spring.cards.model.enums.CardStatus.IN_PROGRESS;
import static com.example.spring.cards.model.enums.ExceptionConstant.CARD_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardServiceHandler implements CardService {
    private final CardRepository cardRepository;

    @Override
    public void createCard(CreateCardRequest request) {
        log.info("ActionLog.createCard.start create card");
        cardRepository.save(CARD_MAPPER.buildCardEntity(request));
        log.info("ActionLog.createCard.success create card");
    }

    @Override
    public CardResponse getCard(Long cardId) {
        log.info("ActionLog.getCard.start with id: {}", cardId);
        CardEntity card = fetchCardIfExist(cardId);
        log.info("ActionLog.getCard.success with id: {}", cardId);
        return CARD_MAPPER.buildCardResponse(card);
    }

    @Override
    public List<CardResponse> getCards() {
        log.info("ActionLog.getCards.start fetching all cards");
        List<CardEntity> cards = cardRepository.findAll();
        return cards.stream()
                .map(CARD_MAPPER::buildCardResponse)
                .toList();
    }

    @Override
    public void deleteCard(Long cardId) {
        log.info("ActionLog.deleteCard.start with id: {}", cardId);
        CardEntity card = fetchCardIfExist(cardId);
        card.setStatus(DELETED);
        log.info("ActionLog.deleteCard.success with id: {}", cardId);
        cardRepository.save(card);
    }

    @Override
    public void updateCard(Long cardId, CreateCardRequest request) {
        log.info("ActionLog.updateCard.start with id: {}", cardId);
        CardEntity card = fetchCardIfExist(cardId);
        card.setStatus(IN_PROGRESS);

        if (request.getCardNumber() != null) {
            card.setCardNumber(request.getCardNumber());
        }
        if (request.getCardholderName() != null) {
            card.setCardholderName(request.getCardholderName());
        }
        if (request.getExpiryDate() != null) {
            card.setExpiryDate(request.getExpiryDate());
        }
        if (request.getCvv() != null) {
            card.setCvv(request.getCvv());
        }
        if (request.getCardType() != null) {
            card.setCardType(request.getCardType());
        }
        if (request.getIssueDate() != null) {
            card.setIssueDate(request.getIssueDate());
        }
        if (request.getBalance() != null) {
            card.setBalance(request.getBalance());
        }
        log.info("ActionLog.updateCard.success with id: {}", cardId);
        cardRepository.save(card);
    }

    @Override
    public void increaseCardBalances() {
        List<CardEntity> cards = cardRepository.findAll();
        cards.forEach(card -> {
            BigDecimal percentage = new BigDecimal("0.05");
            BigDecimal currentBalance = card.getBalance();
            BigDecimal increaseAmount = currentBalance.multiply(percentage);
            card.setBalance(currentBalance.add(increaseAmount));
        });
        cardRepository.saveAll(cards);
    }

    private CardEntity fetchCardIfExist(Long cardId) {
        return cardRepository.findById(cardId).orElseThrow(()->
        {
            log.error("ActionLog.fetchCardIfExist.start with id: {} not found", cardId);
            return new NotFoundException(CARD_NOT_FOUND.getCode(), CARD_NOT_FOUND.getMessage());
        });
    }

}

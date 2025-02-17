package com.example.spring.cards.service.abstraction;

import com.example.spring.cards.model.request.CreateCardRequest;
import com.example.spring.cards.model.response.CardResponse;

import java.util.List;

public interface CardService {

    void createCard(CreateCardRequest request);

    CardResponse getCard(Long cardId);

    List<CardResponse> getCards();

    void deleteCard(Long cardId);

    void updateCard(Long cardId, CreateCardRequest request);

    void increaseCardBalances();

}

package com.devsuperior.dslist.services;

import com.devsuperior.dslist.dto.GameListDTO;
import com.devsuperior.dslist.entities.GameList;
import com.devsuperior.dslist.projections.GameMinProjection;
import com.devsuperior.dslist.repositories.GameListRepository;
import com.devsuperior.dslist.repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GameListService {

    @Autowired
    private GameListRepository gameListRepository;

    @Autowired
    private GameRepository gameRepository;

    @Transactional(readOnly = true)
    public List<GameListDTO> findAll() {
        List<GameList> result = gameListRepository.findAll();
        return result.stream().map(GameListDTO::new).toList();
    }

    @Transactional
    public void move(Long listId, int sourceIndex, int destinationIndex) {
        List<GameMinProjection> list = gameRepository.searchByList(listId);

        if (sourceIndex < 0 || destinationIndex < 0 || sourceIndex >= list.size() || destinationIndex >= list.size()) {
            throw new IllegalArgumentException("Índices inválidos para movimentação.");
        }

        GameMinProjection obj = list.get(sourceIndex);
        Long gameId = obj.getId();

        list.remove(sourceIndex);
        list.add(destinationIndex, obj);

        int start = Math.min(sourceIndex, destinationIndex);
        int end = Math.max(sourceIndex, destinationIndex);

        for (int i = start; i <= end; i++) {
            gameListRepository.updateBelongingPosition(listId, list.get(i).getId(), i);
        }
    }

    }

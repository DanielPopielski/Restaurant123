package com.restaurant.service;

import com.restaurant.dto.TableRequest;
import com.restaurant.dto.TableResponse;
import com.restaurant.entity.TableEntity;
import com.restaurant.exception.ConflictException;
import com.restaurant.exception.ResourceNotFoundException;
import com.restaurant.repository.TableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TableService {

    private final TableRepository tableRepository;

    public List<TableResponse> findAll() {
        return tableRepository.findAll().stream().map(this::toResponse).toList();
    }

    public TableResponse findById(Long id) {
        return toResponse(getTable(id));
    }

    public TableResponse create(TableRequest request) {
        if (tableRepository.existsByTableNumber(request.tableNumber())) {
            throw new ConflictException("Table number " + request.tableNumber() + " already exists");
        }
        TableEntity table = new TableEntity();
        table.setTableNumber(request.tableNumber());
        table.setSeats(request.seats());
        return toResponse(tableRepository.save(table));
    }

    public TableResponse update(Long id, TableRequest request) {
        TableEntity table = getTable(id);
        table.setTableNumber(request.tableNumber());
        table.setSeats(request.seats());
        return toResponse(tableRepository.save(table));
    }

    public void delete(Long id) {
        tableRepository.delete(getTable(id));
    }

    private TableEntity getTable(Long id) {
        return tableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table with id " + id + " not found"));
    }

    private TableResponse toResponse(TableEntity table) {
        return new TableResponse(table.getId(), table.getTableNumber(), table.getSeats());
    }
}

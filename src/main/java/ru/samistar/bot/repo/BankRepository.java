package ru.samistar.bot.repo;

import org.springframework.data.repository.CrudRepository;
import ru.samistar.bot.modal.Bank;

import java.util.List;

public interface BankRepository extends CrudRepository<Bank, Integer> {
    List<Bank> findByNameContainsIgnoreCase(String name);
    Bank findByName(String name);
}

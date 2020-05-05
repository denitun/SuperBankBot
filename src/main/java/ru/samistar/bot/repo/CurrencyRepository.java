package ru.samistar.bot.repo;

import org.springframework.data.repository.CrudRepository;
import ru.samistar.bot.modal.Currency;

public interface CurrencyRepository extends CrudRepository<Currency, Integer> {
}

package ru.samistar.bot.repo;

import org.springframework.data.repository.CrudRepository;
import ru.samistar.bot.modal.Bank;
import ru.samistar.bot.modal.Favorites;

public interface FavoritesRepository extends CrudRepository<Favorites, Integer> {
}

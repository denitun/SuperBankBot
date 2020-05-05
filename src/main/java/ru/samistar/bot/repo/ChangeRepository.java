package ru.samistar.bot.repo;

import org.springframework.data.repository.CrudRepository;
import ru.samistar.bot.modal.Change;

public interface ChangeRepository extends CrudRepository<Change, Integer> {
}

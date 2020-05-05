package ru.samistar.bot.repo;

import org.springframework.data.repository.CrudRepository;
import ru.samistar.bot.modal.Favorites;
import ru.samistar.bot.modal.User;

public interface UserRepository extends CrudRepository<User, Integer> {
    User findByChatId(Integer chatId);
}

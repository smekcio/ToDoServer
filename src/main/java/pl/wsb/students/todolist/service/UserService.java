package pl.wsb.students.todolist.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.wsb.students.todolist.models.User;
import pl.wsb.students.todolist.repository.UserRepository;

import javax.transaction.Transactional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(TaskListService.class);

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByUsername(String username){
        try{
            User user = userRepository.findByUsername(username).orElseThrow();
            return user;
        } catch (Exception e){
            logger.error("User called \"{}\" not found", username);
        }
        return null;
    }

    public User findById(Long id){
        try{
            User user = userRepository.findById(id).orElseThrow();
            return user;
        } catch (Exception e){
            logger.error("User with id \"{}\" not found", id);
        }
        return null;
    }
}

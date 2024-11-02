package com.irusri.todo.service;

import com.irusri.todo.entity.Todo;
import com.irusri.todo.entity.User;
import com.irusri.todo.exception.ResourceNotFoundException;
import com.irusri.todo.exception.UnauthorizedAccessException;
import com.irusri.todo.repository.TodoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TodoService {

    private static final Logger logger = LoggerFactory.getLogger(TodoService.class);

    @Autowired
    private TodoRepository todoRepository;

    public Todo createTodo(Todo todo, User user) {
        todo.setUser(user);
        Todo savedTodo = todoRepository.save(todo);
        logger.info("Created new Todo with ID {} for user ID {}", savedTodo.getId(), user.getId());
        return savedTodo;
    }

    public Page<Todo> getTodos(User user, Pageable pageable) {
        Page<Todo> todos = todoRepository.findByUserId(user.getId(), pageable);
        logger.info("Retrieved {} Todos for user ID {}", todos.getTotalElements(), user.getId());
        return todos;
    }

    public List<Todo> searchTodos(String keyword, User user) {
        List<Todo> todos = todoRepository.findByUserId(user.getId()).stream()
                .filter(todo -> todo.getTitle().contains(keyword) || todo.getDescription().contains(keyword))
                .toList();
        logger.info("Found {} Todos for user ID {} with keyword '{}'", todos.size(), user.getId(), keyword);
        return todos;
    }

    public Todo updateTodo(Long id, Todo todoDetails, User user) {
        Todo todo = getTodoByIdAndUser(id, user);

        todo.setTitle(todoDetails.getTitle());
        todo.setDescription(todoDetails.getDescription());
        todo.setDueDate(todoDetails.getDueDate());
        todo.setComplete(todoDetails.isComplete());
        todo.setPriority(todoDetails.getPriority());

        Todo updatedTodo = todoRepository.save(todo);
        logger.info("Updated Todo with ID {} for user ID {}", updatedTodo.getId(), user.getId());
        return updatedTodo;
    }
    public void deleteTodo(Long id, User user) {
        Todo todo = getTodoByIdAndUser(id, user);
        todoRepository.delete(todo);
        logger.info("Deleted Todo with ID {} for user ID {}", id, user.getId());
    }

    private Todo getTodoByIdAndUser(Long id, User user) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Todo with ID " + id + " not found"));

        if (!todo.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to access this Todo");
        }
        return todo;
    }
}

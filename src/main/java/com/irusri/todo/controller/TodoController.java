package com.irusri.todo.controller;

import com.irusri.todo.entity.Todo;
import com.irusri.todo.entity.User;
import com.irusri.todo.repository.UserRepository;
import com.irusri.todo.service.TodoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class TodoController {
    private static final Logger logger = LoggerFactory.getLogger(TodoController.class);
    @Autowired
    private TodoService todoService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create")
    public Todo createTodo(@RequestBody Todo todo, Authentication authentication) {
        User user = getUserFromAuth(authentication);
        return todoService.createTodo(todo, user);
    }

    @GetMapping("/get")
    public Page<Todo> getTodos(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(defaultValue = "dueDate") String sortBy,
                               @RequestParam(defaultValue = "asc") String direction,
                               Authentication authentication) {
        User user = getUserFromAuth(authentication);
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(direction), sortBy);
        return todoService.getTodos(user, pageable);
    }

    @GetMapping("/search")
    public List<Todo> searchTodos(@RequestParam String keyword, Authentication authentication) {
        User user = getUserFromAuth(authentication);
        return todoService.searchTodos(keyword, user);
    }

    @PutMapping("/update/{id}")
    public Todo updateTodo(@PathVariable Long id, @RequestBody Todo todoDetails, Authentication authentication) {
        User user = getUserFromAuth(authentication);
        return todoService.updateTodo(id, todoDetails, user);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteTodo(@PathVariable Long id, Authentication authentication) {
        User user = getUserFromAuth(authentication);
        todoService.deleteTodo(id, user);
        return "Todo deleted successfully";
    }

    private User getUserFromAuth(Authentication authentication) {
        logger.debug("Fetching user details for email: {}", authentication.getName());
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
package com.irusri.todo.service;

import com.irusri.todo.entity.Todo;
import com.irusri.todo.entity.User;
import com.irusri.todo.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
    }

    @Test
    void testCreateTodo_Success() {
        Todo todo = new Todo();
        todo.setTitle("Sample Todo");
        todo.setUser(user);

        when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        Todo createdTodo = todoService.createTodo(todo, user);
        assertNotNull(createdTodo);
        assertEquals("Sample Todo", createdTodo.getTitle());
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    void testDeleteTodo_Success() {
        Todo todo = new Todo();
        todo.setId(1L);
        todo.setUser(user);

        when(todoRepository.findById(todo.getId())).thenReturn(Optional.of(todo));

        todoService.deleteTodo(todo.getId(), user);
        verify(todoRepository, times(1)).delete(todo);
    }

    @Test
    void testDeleteTodo_Unauthorized() {
        Todo todo = new Todo();
        todo.setId(1L);
        User anotherUser = new User();
        anotherUser.setId(2L);
        todo.setUser(anotherUser);

        when(todoRepository.findById(todo.getId())).thenReturn(Optional.of(todo));

        Exception exception = assertThrows(RuntimeException.class, () -> todoService.deleteTodo(todo.getId(), user));
        assertEquals("Unauthorized to access this todo", exception.getMessage());
        verify(todoRepository, never()).delete(todo);
    }
}
